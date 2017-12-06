package me.cbitler.raidbot.selection;

import me.cbitler.raidbot.raids.Raid;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Step for a user picking a flex role for a raid
 */
public class PickFlexRoleStep implements SelectionStep {
    Raid raid;
    String spec;

    /**
     * Create a new step for this flex role selection with the specified raid and spec
     * that the user chose
     * @param raid The raid
     * @param spec The specialization that the user chose
     */
    public PickFlexRoleStep(Raid raid, String spec) {
        this.raid = raid;
        this.spec = spec;
    }

    /**
     * Handle the user input - checks to see if the role they are picking is valid
     * and if so, adding them to that flex role
     * @param e The private message event
     * @return True if the user chose a valid role, false otherwise
     */
    @Override
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        if(raid.isValidRole(e.getMessage().getRawContent())) {
            raid.addUserFlexRole(e.getAuthor().getId(), e.getAuthor().getName(), spec, e.getMessage().getRawContent(), true, true);
            e.getChannel().sendMessage("Added to raid roster as flex role.").queue();
            return true;
        } else {
            e.getChannel().sendMessage("Please choose a valid flex role.").queue();
            return false;
        }
    }

    /**
     * Get the next step - null here as this is a one-step process
     * @return null
     */
    @Override
    public SelectionStep getNextStep() {
        return null;
    }

    /**
     * The step text changes the text based on the available roles.
     * @return The step text
     */
    @Override
    public String getStepText() {
        String text = "Pick a flex role (";
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
