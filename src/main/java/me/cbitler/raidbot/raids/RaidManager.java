package me.cbitler.raidbot.raids;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.database.Database;
import me.cbitler.raidbot.database.QueryResult;
import me.cbitler.raidbot.utility.Reactions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RaidManager {

    static List<Raid> raids = new ArrayList<>();
    public static void createRaid(PendingRaid raid) {
        MessageEmbed message = buildEmbed(raid);

        Guild guild = RaidBot.getInstance().getServer(raid.getServerId());
        List<TextChannel> channels = guild.getTextChannelsByName(raid.getAnnouncementChannel(), true);
        if(channels.size() > 0) {
            channels.get(0).sendMessage(message).queue(message1 -> {
                boolean inserted = insertToDatabase(raid, message1.getId(), message1.getGuild().getId(), message1.getChannel().getId());
                if (inserted) {
                    Raid newRaid = new Raid(message1.getId(), message1.getGuild().getId(), message1.getChannel().getId(), raid.getName(), raid.getDate(), raid.getTime());
                    newRaid.roles.addAll(raid.rolesWithNumbers);
                    raids.add(newRaid);
                    List<String> emojis = Reactions.getEmojis();
                    List<Emote> emotes = message1.getGuild().getEmotes();
                    for(Emote s : emotes) {
                        if(emojis.contains(s.getName()) || s.getName().equalsIgnoreCase("X_")) {
                            message1.addReaction(s).queue();
                        }
                    }
                } else {
                    message1.delete().queue();
                }
            });
        }
    }

    private static boolean insertToDatabase(PendingRaid raid, String messageId, String serverId, String channelId) {
        RaidBot bot = RaidBot.getInstance();
        Database db = bot.getDatabase();

        String roles = formatRolesForDatabase(raid.getRolesWithNumbers());

        try {
            db.update("INSERT INTO `raids` (`raidId`, `serverId`, `channelId`, `name`, `date`, `time`, `roles`) VALUES (?,?,?,?,?,?,?)", new String[] {
                    messageId,
                    serverId,
                    channelId,
                    raid.getName(),
                    raid.getDate(),
                    raid.getTime(),
                    roles
            });
        } catch (SQLException e) {
            return false;
        }

        return true;
    }

    public static void loadRaids() {
        RaidBot bot = RaidBot.getInstance();
        Database db = bot.getDatabase();

        try {
            QueryResult results = db.query("SELECT * FROM `raids`", new String[] {});
            while (results.getResults().next()) {
                String name = results.getResults().getString("name");
                String date = results.getResults().getString("date");
                String time = results.getResults().getString("time");
                String rolesText = results.getResults().getString("roles");
                String messageId = results.getResults().getString("raidId");
                String serverId = results.getResults().getString("serverId");
                String channelId = results.getResults().getString("channelId");

                Raid raid = new Raid(messageId, serverId, channelId, name, date, time);
                String[] roleSplit = rolesText.split(";");
                for(String roleAndAmount : roleSplit) {
                    String[] parts = roleAndAmount.split(":");
                    int amnt = Integer.parseInt(parts[0]);
                    String role = parts[1];
                    raid.roles.add(new RaidRole(amnt, role));
                }
                raids.add(raid);
            }
            results.getResults().close();
            results.getStmt().close();

            QueryResult userResults = db.query("SELECT * FROM `raidUsers`", new String[] {});

            while(userResults.getResults().next()) {
                String id = userResults.getResults().getString("userId");
                String name = userResults.getResults().getString("username");
                String spec = userResults.getResults().getString("spec");
                String role = userResults.getResults().getString("role");
                String raidId = userResults.getResults().getString("raidId");

                Raid raid = RaidManager.getRaid(raidId);
                if(raid != null) {
                    raid.addUser(id, name, spec, role, false);
                }
            }
        } catch (SQLException e) {
            System.out.println("Couldn't load raids.. exiting");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static Raid getRaid(String messageId)
    {
        for(Raid raid : raids) {
            if (raid.messageId.equalsIgnoreCase(messageId)) {
                return raid;
            }
        }
        return null;
    }
    private static String formatRolesForDatabase(List<RaidRole> rolesWithNumbers) {
        String data = "";

        for (int i = 0; i < rolesWithNumbers.size(); i++) {
            RaidRole role = rolesWithNumbers.get(i);
            if(i == rolesWithNumbers.size() - 1) {
                data += (role.amount + ":" + role.name);
            } else {
                data += (role.amount + ":" + role.name + ";");
            }
        }

        return data;
    }

    private static MessageEmbed buildEmbed(PendingRaid raid) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(raid.getName());
        builder.addField("Date: ", raid.getDate(), true);
        builder.addField("Time: ", raid.getTime(), true);
        builder.addField("Roles:", buildRolesText(raid), false);
        builder.addBlankField(false);
        return builder.build();
    }

    private static String buildRolesText(PendingRaid raid) {
        String text = "";
        for(RaidRole role : raid.getRolesWithNumbers()) {
            text += ("**" + role.name + " (" + role.amount + "):** \n");
        }
        return text;
    }
}
