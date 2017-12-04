package me.cbitler.raidbot.creation;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.PendingRaid;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

public class RunNameStep implements CreationStep {

    String serverId;

    public RunNameStep(String serverId) {
        this.serverId = serverId;
    }

    public boolean handleDM(PrivateMessageReceivedEvent e) {
        RaidBot bot = RaidBot.getInstance();
        PendingRaid raid = bot.getPendingRaids().get(e.getAuthor().getId());
        if(raid == null) {
            raid = new PendingRaid();
            bot.getPendingRaids().put(e.getAuthor().getId(), raid);
        }

        raid.setName(e.getMessage().getRawContent());
        raid.setServerId(serverId);

        return true;
    }

    public String getStepText() {
        return "Enter the name for raid run:";
    }

    public CreationStep getNextStep() {
        return new RunChannelStep();
    }
}
