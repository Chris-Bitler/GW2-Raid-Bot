package me.cbitler.raidbot.edit;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Edit the name for the raid
 * @author Franziska Mueller
 */
public class EditNameStep implements EditStep {

	private String m_messageID;
	
	public EditNameStep(String messageId) {
		m_messageID = messageId;
	}
	
    /**
     * Handle changing the name for the raid
     * @param e The direct message event
     * @return True if the name is set, false otherwise
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        Raid raid = RaidManager.getRaid(m_messageID);
        raid.setName(e.getMessage().getRawContent());
        if (raid.updateNameDB()) {
        	e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Name successfully updated in database.").queue());
        } else {
        	e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Name could not be updated in database.").queue());	
        }
        raid.updateMessage();

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getStepText() {
        return "Enter the new name for raid run:";
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
