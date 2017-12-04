package me.cbitler.raidbot.handlers;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.creation.CreationStep;
import me.cbitler.raidbot.creation.RunNameStep;
import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ChannelMessageHandler extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        RaidBot bot = RaidBot.getInstance();
        if (e.getAuthor().isBot()) {
           return;
        }
        if (hasRaidLeaderRole(e.getMember())) {
            if (e.getMessage().getRawContent().equalsIgnoreCase("!createRaid")) {
                CreationStep runNameStep = new RunNameStep(e.getMessage().getGuild().getId());
                e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(runNameStep.getStepText()).queue());
                bot.getCreationMap().put(e.getAuthor().getId(), runNameStep);
                e.getMessage().delete().queue();
            } else if (e.getMessage().getRawContent().toLowerCase().startsWith("!removefromraid")) {
                String[] split = e.getMessage().getRawContent().split(" ");
                if(split.length < 3) {
                    e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Format for !removeFromRaid: !removeFromRaid [raid id] [name]").queue());
                } else {
                    String messageId = split[1];
                    String name = split[2];

                    Raid raid = RaidManager.getRaid(messageId);

                    if (raid != null) {
                        if(raid.getUserByName(name) != null) {
                            raid.removeUserByName(name);
                        }
                    } else {
                        e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Non-existant raid").queue());
                    }
                }
                e.getMessage().delete().queue();
            }
        }
    }

    private boolean hasRaidLeaderRole(Member member) {
        for (Role role : member.getRoles()) {
            if (role.getName().equalsIgnoreCase(RaidBot.RAID_LEADER_ROLE_TEXT)) {
                return true;
            }
        }
         return false;
    }
}
