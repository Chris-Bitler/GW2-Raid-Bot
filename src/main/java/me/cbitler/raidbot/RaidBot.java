package me.cbitler.raidbot;

import me.cbitler.raidbot.creation.CreationStep;
import me.cbitler.raidbot.database.Database;
import me.cbitler.raidbot.handlers.ChannelMessageHandler;
import me.cbitler.raidbot.handlers.DMHandler;
import me.cbitler.raidbot.handlers.ReactionHandler;
import me.cbitler.raidbot.raids.PendingRaid;
import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.selection.SelectionStep;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;

public class RaidBot {
    public static final String RAID_LEADER_ROLE_TEXT = "Raid Leader";

    private static RaidBot instance;
    private JDA jda;

    HashMap<String, CreationStep> creation = new HashMap<String, CreationStep>();
    HashMap<String, PendingRaid> pendingRaids = new HashMap<String, PendingRaid>();
    HashMap<String, SelectionStep> roleSelection = new HashMap<String, SelectionStep>();

    Database db;

    public RaidBot(JDA jda) {
        instance = this;

        this.jda = jda;
        jda.addEventListener(new DMHandler(this), new ChannelMessageHandler(), new ReactionHandler());
        db = new Database("raid.db");
        db.connect();
        RaidManager.loadRaids();
    }

    public HashMap<String, CreationStep> getCreationMap() {
        return creation;
    }

    public HashMap<String, SelectionStep> getRoleSelectionMap() {
        return roleSelection;
    }

    public HashMap<String, PendingRaid> getPendingRaids() {
        return pendingRaids;
    }

    public Guild getServer(String id) {
        return jda.getGuildById(id);
    }

    public Database getDatabase() {
        return db;
    }

    public static RaidBot getInstance() {
        return instance;
    }

}
