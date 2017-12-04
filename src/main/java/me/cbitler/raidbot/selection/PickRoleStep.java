package me.cbitler.raidbot.selection;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidUser;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

public class PickRoleStep implements SelectionStep {
    Raid raid;
    String spec;

    public PickRoleStep(Raid raid, String spec) {
        this.raid = raid;
        this.spec = spec;
    }

    @Override
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        if(raid.isValidNotFullRole(e.getMessage().getRawContent())) {
            raid.addUser(e.getAuthor().getId(), e.getAuthor().getName(), spec, e.getMessage().getRawContent(), true);
            e.getChannel().sendMessage("Added to raid roster").queue();
            return true;
        } else {
            e.getChannel().sendMessage("Please choose a valid role that is not full.").queue();
            return false;
        }
    }

    @Override
    public SelectionStep getNextStep() {
        return null;
    }

    @Override
    public String getStepText() {
        String text = "Pick a role(";
        for (int i = 0; i < raid.getRoles().size(); i++) {
            if (i == raid.getRoles().size()-1) {
                text += raid.getRoles().get(i).getName();
            } else {
                text += (raid.getRoles().get(i).getName() + ", ");
            }
        }
        text += ")";

        return text;
    }
}
