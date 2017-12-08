package me.cbitler.raidbot.raids;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.database.Database;
import me.cbitler.raidbot.database.QueryResult;
import me.cbitler.raidbot.utility.Reactions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import javax.imageio.stream.IIOByteBuffer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Serves as a manager for all of the raids. This includes creating, loading, and deleting raids
 * @author Christopher Bitler
 */
public class RaidManager {

    static List<Raid> raids = new ArrayList<>();

    /**
     * Create a raid. This turns a PendingRaid object into a Raid object and inserts it into the list of raids.
     * It also sends the associated embedded message and adds the reactions for people to join to the embed
     * @param raid The pending raid to create
     */
    public static void createRaid(PendingRaid raid) {
        MessageEmbed message = buildEmbed(raid);

        Guild guild = RaidBot.getInstance().getServer(raid.getServerId());
        List<TextChannel> channels = guild.getTextChannelsByName(raid.getAnnouncementChannel(), true);
        if(channels.size() > 0) {
            // We always go with the first channel if there is more than one
            try {
                channels.get(0).sendMessage(message).queue(message1 -> {
                    boolean inserted = insertToDatabase(raid, message1.getId(), message1.getGuild().getId(), message1.getChannel().getId());
                    if (inserted) {
                        Raid newRaid = new Raid(message1.getId(), message1.getGuild().getId(), message1.getChannel().getId(), raid.getLeaderName(), raid.getName(), raid.getDate(), raid.getTime());
                        newRaid.roles.addAll(raid.rolesWithNumbers);
                        raids.add(newRaid);

                        for (Emote emote : Reactions.getEmotes()) {
                            message1.addReaction(emote).queue();
                        }
                    } else {
                        message1.delete().queue();
                    }
                });
            } catch (Exception e) {
                System.out.println("Error encountered in sending message.");
                e.printStackTrace();
                throw e;
            }
        }
    }

    /**
     * Insert a raid into the database
     * @param raid The raid to insert
     * @param messageId The embedded message / 'raidId'
     * @param serverId The serverId related to this raid
     * @param channelId The channelId for the announcement of this raid
     * @return True if inserted, false otherwise
     */
    private static boolean insertToDatabase(PendingRaid raid, String messageId, String serverId, String channelId) {
        RaidBot bot = RaidBot.getInstance();
        Database db = bot.getDatabase();

        String roles = formatRolesForDatabase(raid.getRolesWithNumbers());

        try {
            db.update("INSERT INTO `raids` (`raidId`, `serverId`, `channelId`, `leader`, `name`, `date`, `time`, `roles`) VALUES (?,?,?,?,?,?,?,?)", new String[] {
                    messageId,
                    serverId,
                    channelId,
                    raid.getLeaderName(),
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

    /**
     * Load raids
     * This first queries all of the raids and loads the raid data and adds the raids to the raid list
     * Then, it queries the raid users and inserts them into their relevant raids, updating the embedded messages
     * Finally, it queries the raid users' flex roles and inserts those to the raids
     */
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

                String leaderName = null;
                try {
                    leaderName = results.getResults().getString("leader");
                } catch (Exception e) { }

                Raid raid = new Raid(messageId, serverId, channelId, leaderName, name, date, time);
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
                    raid.addUser(id, name, spec, role, false, false);
                }
            }

            QueryResult userFlexRolesResults = db.query("SELECT * FROM `raidUsersFlexroles`", new String[] {});

            while(userFlexRolesResults.getResults().next()) {
                String id = userFlexRolesResults.getResults().getString("userId");
                String name = userFlexRolesResults.getResults().getString("username");
                String spec = userFlexRolesResults.getResults().getString("spec");
                String role = userFlexRolesResults.getResults().getString("role");
                String raidId = userFlexRolesResults.getResults().getString("raidId");

                Raid raid = RaidManager.getRaid(raidId);
                if(raid != null) {
                    raid.addUserFlexRole(id, name, spec, role, false, false);
                }
            }

            for(Raid raid : raids) {
                raid.updateMessage();
            }
        } catch (SQLException e) {
            System.out.println("Couldn't load raids.. exiting");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Delete the raid from the database and maps, and delete the message if it is still there
     * @param messageId The raid ID
     * @return true if deleted, false if not deleted
     */
    public static boolean deleteRaid(String messageId) {
        Raid r = getRaid(messageId);
        if (r != null) {
            try {
                RaidBot.getInstance().getServer(r.getServerId())
                        .getTextChannelById(r.getChannelId()).getMessageById(messageId).queue(message -> message.delete().queue());
            } catch (Exception e) {
                // Nothing, the message doesn't exist - it can happen
            }

            Iterator<Raid> raidIterator = raids.iterator();
            while (raidIterator.hasNext()) {
                Raid raid = raidIterator.next();
                if (raid.getMessageId().equalsIgnoreCase(messageId)) {
                    raidIterator.remove();
                }
            }

            try {
                RaidBot.getInstance().getDatabase().update("DELETE FROM `raids` WHERE `raidId` = ?", new String[]{
                        messageId
                });
                RaidBot.getInstance().getDatabase().update("DELETE FROM `raidUsers` WHERE `raidId` = ?", new String[]{
                        messageId
                });
                RaidBot.getInstance().getDatabase().update("DELETE FROM `raidUsersFlexRoles` WHERE `raidId` = ?",
                        new String[]{messageId});
            } catch (Exception e) {
                System.out.println("Error encountered deleting raid");
            }

            return true;
        }

        return false;
    }

    /**
     * Get a raid from the discord message ID
     * @param messageId The discord message ID associated with the raid's embedded message
     * @return The raid object related to that messageId, if it exist.
     */
    public static Raid getRaid(String messageId)
    {
        for(Raid raid : raids) {
            if (raid.messageId.equalsIgnoreCase(messageId)) {
                return raid;
            }
        }
        return null;
    }

    /**
     * Formats the roles associated with a raid in a form that can be inserted into a database row.
     * This combines them as [number]:[name];[number]:[name];...
     * @param rolesWithNumbers The roles and their amounts
     * @return The formatted string
     */
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

    /**
     * Create a message embed to show the raid
     * @param raid The raid object
     * @return The embedded message
     */
    private static MessageEmbed buildEmbed(PendingRaid raid) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(raid.getName());
        builder.addBlankField(false);
        if (raid.getLeaderName() != null) {
            builder.addField("Leader: ", "**" + raid.getLeaderName() + "**", false);
        }
        builder.addBlankField(false);
        builder.addField("Date: ", raid.getDate(), true);
        builder.addField("Time: ", raid.getTime(), true);
        builder.addBlankField(false);
        builder.addField("Roles: ", buildRolesText(raid), true);
        builder.addField("Flex Roles: ", buildFlexRolesText(raid), true);
        builder.addBlankField(false);
        return builder.build();
    }

    /**
     * Builds the text to go into the roles field in the embedded message
     * @param raid The raid object
     * @return The role text
     */
    private static String buildRolesText(PendingRaid raid) {
        String text = "";
        for(RaidRole role : raid.getRolesWithNumbers()) {
            text += ("**" + role.name + " (" + role.amount + "):** \n");
        }
        return text;
    }

    /**
     * Build the flex role text. This is blank here as we have no flex roles at this point.
     * @param raid
     * @return The flex roles text (blank here)
     */
    private static String buildFlexRolesText(PendingRaid raid) {
        return "";
    }
}
