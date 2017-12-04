package me.cbitler.raidbot.handlers;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.selection.PickRoleStep;
import me.cbitler.raidbot.selection.SelectionStep;
import me.cbitler.raidbot.utility.Reactions;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.function.Consumer;

public class ReactionHandler extends ListenerAdapter {
    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        Raid raid = RaidManager.getRaid(e.getMessageId());
        if(e.getUser().isBot()) {
            return;
        }
        if (raid != null) {
            if (Reactions.getEmojis().contains(e.getReactionEmote().getEmote().getName())) {
                RaidBot bot = RaidBot.getInstance();
                if(!raid.isUserInRaid(e.getUser().getId())) {
                    if (bot.getRoleSelectionMap().get(e.getUser().getId()) == null) {
                        SelectionStep step = new PickRoleStep(raid, e.getReactionEmote().getEmote().getName());
                        bot.getRoleSelectionMap().put(e.getUser().getId(), step);
                        e.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(step.getStepText()).queue());
                    } else {
                        e.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("You are already selecting a role.").queue());
                    }
                } else {
                    e.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("You are already in this raid. Click the X to select a new role").queue());
                }
            } else if(e.getReactionEmote().getEmote().getName().equalsIgnoreCase("X_")) {
                raid.removeUser(e.getUser().getId());
            }

            e.getReaction().removeReaction(e.getUser()).queue();
        }
    }

}
