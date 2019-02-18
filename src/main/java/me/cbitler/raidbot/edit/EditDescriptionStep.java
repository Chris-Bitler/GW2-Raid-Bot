package me.cbitler.raidbot.edit;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Edit the description for the raid
 * @author Franziska Mueller
 */
public class EditDescriptionStep implements EditStep {

	private String m_messageID;
	
	public EditDescriptionStep(String messageId) {
		m_messageID = messageId;
	}
	
    /**
     * Handle changing the description for the raid
     * @param e The direct message event
     * @return True if the description is set, false otherwise
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        Raid raid = RaidManager.getRaid(m_messageID);
        raid.setDescription(e.getMessage().getRawContent());
        if (raid.updateDescriptionDB()) {
        	e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Description successfully updated in database.").queue());
        } else {
        	e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Description could not be updated in database.").queue());	
        }
        raid.updateMessage();

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getStepText() {
        return "Enter the new description for raid run:";
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
