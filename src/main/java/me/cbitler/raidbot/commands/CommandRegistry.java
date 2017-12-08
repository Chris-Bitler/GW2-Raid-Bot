package me.cbitler.raidbot.commands;

import java.util.HashMap;

public class CommandRegistry {
    public static final String CMD_PREFIX = "!";
    private static final HashMap<String, Command> commands = new HashMap<>();

    public static void addCommand(String commandText, Command cmd) {
        commands.put(commandText.toLowerCase(), cmd);
    }

    public static Command getCommand(String command) {
        return commands.get(command.toLowerCase());
    }

    public static String[] getArguments(String[] messageParts) {
        if(messageParts.length != 1) {
            String[] args = new String[messageParts.length - 1];
            for(int i = 1; i < messageParts.length; i++) {
                args[i-1] = messageParts[i];
            }

            return args;
        } else {
            return new String[0];
        }
    }
}
