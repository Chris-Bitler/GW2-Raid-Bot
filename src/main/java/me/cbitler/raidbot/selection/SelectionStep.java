package me.cbitler.raidbot.selection;

import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Step in the role selection process
 * @author Christopher Bitler
 */
public interface SelectionStep
{
    /**
     * Handle a direct message sent in relation to this step
     * @param e The private message event
     * @return True if the step is done, false otherwise
     */
    boolean handleDM(PrivateMessageReceivedEvent e);

    /**
     * Get the object representing the next step in the role selection process
     * @return The object representing the next step, or null
     */
    SelectionStep getNextStep();

    /**
     * Get the text to show the user about this step
     * @return The text to show the user about this step.
     */
    String getStepText();
}
