package me.cbitler.raidbot.creation;

import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

public interface CreationStep {
    boolean handleDM(PrivateMessageReceivedEvent e);

    CreationStep getNextStep();

    String getStepText();
}
