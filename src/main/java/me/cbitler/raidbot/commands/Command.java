package me.cbitler.raidbot.commands;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public interface Command {
    void handleCommand(String command, String[] args, TextChannel channel, User author);
}
