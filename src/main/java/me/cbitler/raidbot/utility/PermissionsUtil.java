package me.cbitler.raidbot.utility;

import me.cbitler.raidbot.RaidBot;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

/**
 * Utility class for handling permissions
 * @author Christopher Bitler
 */
public class PermissionsUtil {
    /**
     * Check to see if a member has the raid leader role
     * @param member The member to check
     * @return True if they have the role, false if they don't
     */
    public static boolean hasRaidLeaderRole(Member member) {
        String raidLeaderRole = RaidBot.getInstance().getRaidLeaderRole(member.getGuild().getId());
        for (Role role : member.getRoles()) {
            if (role.getName().equalsIgnoreCase(raidLeaderRole)) {
                return true;
            }
        }
        return false;
    }
}
