package me.cbitler.raidbot.utility;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class GuildCountUtil {
    /**
     * Update the server count on DiscordBots.org, based off an example from their discord.
     * @param jda The JDA library instance
     * @throws IOException
     */
    public static void sendGuilds(JDA jda) throws IOException {
        String url = "https://discordbots.org/api/bots/" + jda.getSelfUser().getId() + "/stats";
        int serverCount = jda.getGuilds().size();
        //int shardId = jda.getShardInfo().getShardId();
        //int shardCount = jda.getShardInfo().getShardTotal();
        String token = readToken();
        String query = "{\"server_count\": " + serverCount + "}";
        String charset = java.nio.charset.StandardCharsets.UTF_8.name();

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setDoOutput(true);
        conn.setRequestProperty("Accept-Charset", charset);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", token);

        OutputStream output = conn.getOutputStream();
        output.write(query.getBytes(charset));
        output.close();

        // Note, this is for testing so that it's easy to see what guilds are running the bot in order to help them with issues
        saveGuilds(jda);
    }

    /**
     * Save a list of the guilds using the bot to a file for easier debugging. This can easily be removed.
     * @param jda
     */
    private static void saveGuilds(JDA jda) {
        File file = new File("guilds");
        if(file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();

            FileWriter fw = new FileWriter(file, false);
            for (Guild guild : jda.getGuilds()) {
                fw.write(guild.getName() + "\n");
            }
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Read the token from the token file
     * @return The token text
     * @throws IOException
     */
    private static String readToken() throws IOException {
        BufferedReader br = new BufferedReader(
                new FileReader(new File("discord_bots_token")));
        return br.readLine();
    }
}