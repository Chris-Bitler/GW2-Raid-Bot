package me.cbitler.raidbot.creation;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.PendingRaid;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

public class RunChannelStep implements CreationStep {
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        RaidBot bot = RaidBot.getInstance();
        PendingRaid raid = bot.getPendingRaids().get(e.getAuthor().getId());
        if (raid == null) {
            return false;
        }

        raid.setAnnouncementChannel(e.getMessage().getRawContent());

        return true;
    }

    public String getStepText() {
        return "Enter the channel for raid run announcement:";
    }

    public CreationStep getNextStep() {
        return new RunDateStep();
    }
}
