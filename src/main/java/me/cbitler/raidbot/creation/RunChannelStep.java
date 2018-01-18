package me.cbitler.raidbot.creation;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.PendingRaid;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Get the announcement channel for the raid from the user
 * @author Christopher Bitler
 */
public class RunChannelStep implements CreationStep {
    /**
     * Set the announcement channel
     * @param e The direct message event
     * @return true if the announcement channel was set, false if it was not
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        RaidBot bot = RaidBot.getInstance();
        PendingRaid raid = bot.getPendingRaids().get(e.getAuthor().getId());
        if (raid == null) {
            return false;
        }

        String channelWithoutHash = e.getMessage().getRawContent().replace("#","");
        boolean validChannel = false;
        for (TextChannel channel : bot.getServer(raid.getServerId()).getTextChannels()) {
            if(channel.getName().replace("#","").equalsIgnoreCase(channelWithoutHash)) {
                validChannel = true;
            }
        }

        if(!validChannel) {
            e.getChannel().sendMessage("Please choose a valid channel.").queue();
            return false;
        }

        raid.setAnnouncementChannel(e.getMessage().getRawContent().replace("#",""));

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getStepText() {
        return "Enter the channel for raid run announcement:";
    }

    /**
     * {@inheritDoc}
     */
    public CreationStep getNextStep() {
        return new RunDateStep();
    }
}
