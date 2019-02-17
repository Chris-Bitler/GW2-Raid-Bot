package me.cbitler.raidbot.edit;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.PendingRaid;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Edit a property for the raid
 * @author Franziska Mueller
 */
public class EditIdleStep implements EditStep {

	private String m_messageID;
	private EditStep m_nextStep;
	
	public EditIdleStep(String messageId) {
		m_messageID = messageId;
	}
	
    /**
     * Idle step in editing process
     * @param e The direct message event
     * @return True if the user passed the name of an editable property
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        boolean valid = true;
    	if (e.getMessage().getRawContent().equalsIgnoreCase("time")) {
        	m_nextStep = new EditTimeStep(m_messageID);
        	
        } else if(e.getMessage().getRawContent().equalsIgnoreCase("done")) {
            m_nextStep = null;
        }
        else {
        	e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Invalid property. Supported properties: time.").queue());
        	valid = false;
        }
    	return valid;
    }

    /**
     * {@inheritDoc}
     */
    public String getStepText() {
        return "Enter the name of the property you want to change [time] or done when you want to finish editing.";
    }

    /**
     * {@inheritDoc}
     */
    public EditStep getNextStep() {
        return m_nextStep;
    }

	@Override
	public String getMessageID() {
		return m_messageID;
	}
}
