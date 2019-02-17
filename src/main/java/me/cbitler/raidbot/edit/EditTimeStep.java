package me.cbitler.raidbot.edit;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.PendingRaid;
import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Edit the time for the raid
 * @author Franziska Mueller
 */
public class EditTimeStep implements EditStep {

	private String m_messageID;
	
	public EditTimeStep(String messageId) {
		m_messageID = messageId;
	}
	
    /**
     * Handle changing the time for the raid
     * @param e The direct message event
     * @return True if the time is set, false otherwise
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        Raid raid = RaidManager.getRaid(m_messageID);
        raid.setTime(e.getMessage().getRawContent());
        if (raid.updateTimeDB()) {
        	e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Raid time successfully updated.").queue());
        } else {
        	e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Time could not be updated in database.").queue());	
        }
        raid.updateMessage();

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getStepText() {
        return "Enter the new time for raid run:";
    }

    /**
     * {@inheritDoc}
     */
    public EditStep getNextStep() {
        return new EditIdleStep(m_messageID);
    }

	@Override
	public String getMessageID() {
		return m_messageID;
	}
}
