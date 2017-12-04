package me.cbitler.raidbot.creation;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.PendingRaid;
import me.cbitler.raidbot.raids.RaidRole;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

public class RunRoleSetupStep implements CreationStep {

    public boolean handleDM(PrivateMessageReceivedEvent e) {
        RaidBot bot = RaidBot.getInstance();
        PendingRaid raid = bot.getPendingRaids().get(e.getAuthor().getId());

        if(e.getMessage().getRawContent().equalsIgnoreCase("done")) {
            return true;
        } else {
            String[] parts = e.getMessage().getRawContent().split(":");
            if(parts.length < 2) {
                e.getChannel().sendMessage("You need to specify the role in the format [amount]:[Role name]").queue();
            } else {
                int amnt = Integer.parseInt(parts[0]);
                String roleName = parts[1];
                raid.getRolesWithNumbers().add(new RaidRole(amnt, roleName));
                e.getChannel().sendMessage("Role added").queue();
            }
            return false;
        }
    }

    public String getStepText() {
        return "Enter the roles for raid run (format: [amount]:[Role name]). Type done to finish entering roles:";
    }

    public CreationStep getNextStep() {
        return null;
    }
}
