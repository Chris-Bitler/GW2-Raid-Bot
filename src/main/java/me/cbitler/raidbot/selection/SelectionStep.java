package me.cbitler.raidbot.selection;

import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

public interface SelectionStep
{
    boolean handleDM(PrivateMessageReceivedEvent e);

    SelectionStep getNextStep();

    String getStepText();
}
