package me.cbitler.raidbot;

import me.cbitler.raidbot.commands.*;
import me.cbitler.raidbot.creation.CreationStep;
import me.cbitler.raidbot.edit.EditStep;
import me.cbitler.raidbot.database.Database;
import me.cbitler.raidbot.database.QueryResult;
import me.cbitler.raidbot.handlers.ChannelMessageHandler;
import me.cbitler.raidbot.handlers.DMHandler;
import me.cbitler.raidbot.handlers.ReactionHandler;
import me.cbitler.raidbot.raids.PendingRaid;
import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.selection.SelectionStep;
import me.cbitler.raidbot.utility.GuildCountUtil;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Class representing the raid bot itself.
 * This stores the creation/roleSelection map data and also the list of pendingRaids
 * Additionally, it also stores the database in use by the bot and serves as a way
 * for other classes to access it.
 *
 * @author Christopher Bitler
 * @author Franziska Mueller
 */
public class RaidBot {
    private static RaidBot instance;
    private JDA jda;

    HashMap<String, CreationStep> creation = new HashMap<String, CreationStep>();
    HashMap<String, EditStep> edits = new HashMap<String, EditStep>();
    HashMap<String, PendingRaid> pendingRaids = new HashMap<String, PendingRaid>();
    HashMap<String, SelectionStep> roleSelection = new HashMap<String, SelectionStep>();
    
    Set<String> editList = new HashSet<String>();

    //TODO: This should be moved to it's own settings thing
    HashMap<String, String> raidLeaderRoleCache = new HashMap<>();

    Database db;

    /**
     * Create a new instance of the raid bot with the specified JDA api
     * @param jda The API for the bot to use
     */
    public RaidBot(JDA jda) {
        instance = this;

        this.jda = jda;
        jda.addEventListener(new DMHandler(this), new ChannelMessageHandler(), new ReactionHandler());
        db = new Database("raid.db");
        db.connect();
        RaidManager.loadRaids();
        
        CommandRegistry.addCommand("help", new HelpCommand());
        CommandRegistry.addCommand("info", new InfoCommand());
        CommandRegistry.addCommand("endRaid", new EndRaidCommand());

        new Thread(() -> {
            while (true) {
                try {
                    GuildCountUtil.sendGuilds(jda);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000*60*5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Map of UserId -> creation step for people in the creation process
     * @return The map of UserId -> creation step for people in the creation process
     */
    public HashMap<String, CreationStep> getCreationMap() {
        return creation;
    }
    
    /**
     * Map of UserId -> edit step for raids in the edit process
     * @return The map of UserId -> edit step for raids in the edit process
     */
    public HashMap<String, EditStep> getEditMap() {
        return edits;
    }

    /**
     * Map of the UserId -> roleSelection step for people in the role selection process
     * @return The map of the UserId -> roleSelection step for people in the role selection process
     */
    public HashMap<String, SelectionStep> getRoleSelectionMap() {
        return roleSelection;
    }

    /**
     * Map of the UserId -> pendingRaid step for raids in the setup process
     * @return The map of UserId -> pendingRaid
     */
    public HashMap<String, PendingRaid> getPendingRaids() {
        return pendingRaids;
    }
    
    /**
     * List of messageIDs for raids in the edit process
     * @return List of messageIDs for raids in the edit process
     */
    public Set<String> getEditList() {
        return editList;
    }

    /**
     * Get the JDA server object related to the server ID
     * @param id The server ID
     * @return The server related to that that ID
     */
    public Guild getServer(String id) {
        return jda.getGuildById(id);
    }

    /**
     * Exposes the underlying library. This is mainly necessary for getting Emojis
     * @return The JDA library object
     */
    public JDA getJda() {
        return jda;
    }

    /**
     * Get the database that the bot is using
     * @return The database that the bot is using
     */
    public Database getDatabase() {
        return db;
    }

    /**
     * Get the raid leader role for a specific server.
     * This works by caching the role once it's retrieved once, and returning the default if a server hasn't set one.
     * @param serverId the ID of the server
     * @return The name of the role that is considered the raid leader for that server
     */
    public String getRaidLeaderRole(String serverId) {
        if (raidLeaderRoleCache.get(serverId) != null) {
            return raidLeaderRoleCache.get(serverId);
        } else {
            try {
                QueryResult results = db.query("SELECT `raid_leader_role` FROM `serverSettings` WHERE `serverId` = ?",
                        new String[]{serverId});
                if (results.getResults().next()) {
                    raidLeaderRoleCache.put(serverId, results.getResults().getString("raid_leader_role"));
                    return raidLeaderRoleCache.get(serverId);
                } else {
                    return "Raid Leader";
                }
            } catch (Exception e) {
                return "Raid Leader";
            }
        }
    }

    /**
     * Set the raid leader role for a server. This also updates it in SQLite
     * @param serverId The server ID
     * @param role The role name
     */
    public void setRaidLeaderRole(String serverId, String role) {
        raidLeaderRoleCache.put(serverId, role);
        try {
            db.update("INSERT INTO `serverSettings` (`serverId`,`raid_leader_role`) VALUES (?,?)",
                    new String[] { serverId, role});
        } catch (SQLException e) {
            //TODO: There is probably a much better way of doing this
            try {
                db.update("UPDATE `serverSettings` SET `raid_leader_role` = ? WHERE `serverId` = ?",
                        new String[] { role, serverId });
            } catch (SQLException e1) {
                // Not much we can do if there is also an insert error
            }
        }
    }

    /**
     * Get the current instance of the bot
     * @return The current instance of the bot.
     */
    public static RaidBot getInstance() {
        return instance;
    }

}
