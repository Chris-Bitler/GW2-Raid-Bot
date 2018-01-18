package me.cbitler.raidbot.commands;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class InfoCommand implements Command {
    private final String information = "GW2-Raid-Bot Information:\n" +
            "Author: VoidWhisperer#5905\n" +
            "Discord for Support: https://discord.gg/pWs8tDY\n" +
            "Contact me with any questions.";

    @Override
    public void handleCommand(String command, String[] args, TextChannel channel, User author) {
        channel.sendMessage(information).queue();
    }
}
