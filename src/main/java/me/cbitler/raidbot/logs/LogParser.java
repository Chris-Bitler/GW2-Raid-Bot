package me.cbitler.raidbot.logs;

import me.cbitler.raidbot.utility.Variables;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static me.cbitler.raidbot.utility.Variables.RaidBotProperty.RAIDAR_PASSWORD;
import static me.cbitler.raidbot.utility.Variables.RaidBotProperty.RAIDAR_USERNAME;

/**
 * Upload log files sent to the bot to a local parser, dps.report, and GW2 Raidar
 * For much of this it calls out to the commandline so it tries to sanitize the input first
 * @author Christopher Bitler
 */
public class LogParser implements Runnable {
    private PrivateChannel channel;
    private Message.Attachment attachment;
    private HashMap<String, String> invalidCharacters = new HashMap<>();

    /**
     * Create a new instance of the log parser
     * @param channel The channel the file was sent in
     * @param attachment The file that was sent
     */
    public LogParser(PrivateChannel channel, Message.Attachment attachment) {
        this.channel = channel;
        this.attachment = attachment;
        this.populateHashMap();
    }

    /**
     * Run the upload/parsing process
     */
    @Override
    public void run() {
        channel.sendMessage("EVTC file recieved... downloading file").queue();
        String fileName = attachment.getFileName();
        for(Map.Entry<String,String> invalidCharacter : invalidCharacters.entrySet()) {
            fileName = fileName.replace(invalidCharacter.getKey(), invalidCharacter.getValue());
        }
        if(fileName.contains("..")) {
            channel.sendMessage("Invalid character sequence found in file name '..'").queue();
            return;
        }
        File file = new File("parser/" + attachment.getFileName());

        if(file.exists()) file.delete();
        attachment.download(file);
        channel.sendMessage("File downloaded.. parsing.").queue();

        String finalFileName = "";
        String dpsReportUrl = "";

        try {
            Process p = Runtime.getRuntime().exec("dotnet parser/GuildWars2EliteInsights.dll \"parser/" + attachment.getFileName() + "\" \"/var/www/html/logs/\"");
            System.out.println("dotnet parser/GuildWars2EliteInsights.dll \"parser/" + attachment.getFileName() + "\" \"/var/www/html/logs/\"");
            String line;
            BufferedReader bri = new BufferedReader
                    (new InputStreamReader(p.getInputStream()));
            BufferedReader bre = new BufferedReader
                    (new InputStreamReader(p.getErrorStream()));
            while ((line = bri.readLine()) != null) {
                if(line.contains(".html")) {
                    finalFileName = line;
                }
            }
            bri.close();
            while ((line = bre.readLine()) != null) {
                if(line.contains(".html")) {
                    finalFileName = line;
                }
            }
            bre.close();

            p.waitFor();

            channel.sendMessage("File parsed. HTML Generated.").queue();

            channel.sendMessage("Uploading file to dps.report").queue();

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost fileUpload = new HttpPost("https://dps.report/uploadContent?json=1");
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file",  file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
            HttpEntity multipart = builder.build();
            fileUpload.setEntity(multipart);
            CloseableHttpResponse response = client.execute(fileUpload);
            HttpEntity responseEntity = response.getEntity();
            String responseText = IOUtils.toString(responseEntity.getContent());

            JSONParser parser = new JSONParser();

            try {
                JSONObject dpsReportResponse = (JSONObject) parser.parse(responseText);
                dpsReportUrl = (String) dpsReportResponse.get("permalink");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Variables variables = Variables.getINSTANCE();
            channel.sendMessage("dps.report done. Uploading to gw2raidar").queue();
            String tokenResponse =
                    this.handleCurl(new String[] {"curl", "-s", "-F", "username=" + variables.getStringProperty(RAIDAR_USERNAME.toString()), "-F", "password=" + variables.getStringProperty(RAIDAR_PASSWORD.toString()), "https://www.gw2raidar.com/api/v2/token"});
            System.out.println(tokenResponse);
            JSONObject token =
                    (JSONObject) parser.parse(tokenResponse);
            String tokenString = (String) token.get("token");
            String upload =
                    this.handleCurl(new String[] {"curl", "-s", "-X", "PUT", "-H", "Authorization: Token " + tokenString, "-F", "file=@parser/" + attachment.getFileName(), "https://www.gw2raidar.com/api/v2/encounters/new"});
            JSONObject uploadResult = (JSONObject) parser.parse(upload);
            boolean raidarUploadSuccess = uploadResult.get("detail") == null;

            channel.sendMessage("Local parser: http://logs.v-l.pw/logs/" + finalFileName + "\n ").queue();
            channel.sendMessage("dps.report: " + dpsReportUrl.replace("\\","")).queue();
            if(raidarUploadSuccess) {
                channel.sendMessage("Uploaded to GW2Raidar (no url provided)").queue();
            }
        } catch (IOException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void populateHashMap() {
        invalidCharacters.put(":", "_");
        invalidCharacters.put(";", "_");
        invalidCharacters.put("?", "_");
        invalidCharacters.put("..", ".");
    }

    private String handleCurl(String[] processStrings) throws IOException, InterruptedException {
        String data = "";
        ProcessBuilder pb = new ProcessBuilder(processStrings);
        Process p = pb.start();
        String line;
        BufferedReader bri = new BufferedReader
                (new InputStreamReader(p.getInputStream()));
        BufferedReader bre = new BufferedReader
                (new InputStreamReader(p.getErrorStream()));
        while ((line = bri.readLine()) != null) {
            data += line;
        }
        bri.close();
        while ((line = bre.readLine()) != null) {
            data += line;
        }
        bre.close();

        p.waitFor();

        return data;
    }
}
