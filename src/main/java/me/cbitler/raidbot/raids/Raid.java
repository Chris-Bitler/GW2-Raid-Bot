package me.cbitler.raidbot.raids;

import me.cbitler.raidbot.RaidBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.sql.SQLException;
import java.util.*;

/**
 * Represents a raid and has methods for adding/removing users, user flex roles, etc
 */
public class Raid {
    String messageId, name, date,time, serverId, channelId, raidLeaderName;
    List<RaidRole> roles = new ArrayList<RaidRole>();
    HashMap<RaidUser, String> userToRole = new HashMap<RaidUser, String>();
    HashMap<RaidUser, List<FlexRole>> usersToFlexRoles = new HashMap<>();

    /**
     * Create a new Raid with the specified data
     * @param messageId The embedded message Id related to this raid
     * @param serverId The serverId that the raid is on
     * @param channelId The announcement channel's id for this raid
     * @param raidLeaderName The name of the raid leader
     * @param name The name of the raid
     * @param date The date of the raid
     * @param time The time of the raid
     */
    public Raid(String messageId, String serverId, String channelId, String raidLeaderName, String name, String date, String time) {
        this.messageId = messageId;
        this.serverId = serverId;
        this.channelId = channelId;
        this.raidLeaderName = raidLeaderName;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    /**
     * Get the message ID for this raid
     * @return The message ID for this raid
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Get the server ID for this raid
     * @return The server ID for this raid
     */
    public String getServerId() { return serverId; }

    /**
     * Get the channel ID for this raid
     * @return The channel ID for this raid
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * Get the name of this raid
     * @return The name of this raid
     */
    public String getName() {
        return name;
    }

    /**
     * Get the date of this raid
     * @return The date of this raid
     */
    public String getDate() {
        return date;
    }

    /**
     * Get the time of this raid
     * @return The time of this raid
     */
    public String getTime() {
        return time;
    }

    /**
     * Get the raid leader's name
     * @return The raid leader's name
     */
    public String getRaidLeaderName() {
        return raidLeaderName;
    }

    /**
     * Get the list of roles in this raid
     * @return The list of roles in this raid
     */
    public List<RaidRole> getRoles() {
        return roles;
    }

    /**
     * Check if a specific role is valid, and whether or not it's full
     * @param role The role to check
     * @return True if it is valid and not full, false otherwise
     */
    public boolean isValidNotFullRole(String role) {
        RaidRole r = getRole(role);

        if(r != null) {
            int max = r.getAmount();
            if(getUserNumberInRole(role) < max) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check to see if a role is valid
     * @param role The role name
     * @return True if the role is valid, false otherwise
     */
    public boolean isValidRole(String role) {
        return getRole(role) != null;
    }

    /**
     * Get the object representing a role
     * @param role The name of the role
     * @return The object representing the specified role
     */
    private RaidRole getRole(String role) {
        for(RaidRole r : roles) {
            if(r.getName().equalsIgnoreCase(role)) {
                return r;
            }
        }

        return null;
    }

    /**
     * Get the number of users in a role
     * @param role The name of the role
     * @return The number of users in the role
     */
    private int getUserNumberInRole(String role) {
        int inRole = 0;
        for(Map.Entry<RaidUser, String> entry : userToRole.entrySet()) {
            if(entry.getValue().equalsIgnoreCase(role)) inRole+=1;
        }

        return inRole;
    }

    /**
     * Get list of users in a role
     * @param role The name of the role
     * @return The users in the role
     */
    public List<RaidUser> getUsersInRole(String role) {
        List<RaidUser> users = new ArrayList<>();
        for(Map.Entry<RaidUser, String> entry : userToRole.entrySet()) {
            if(entry.getValue().equalsIgnoreCase(role)) {
                users.add(entry.getKey());
            }
        }

        return users;
    }

    /**
     * Add a user to this raid.
     * This first creates the user and attempts to insert it into the database, if needed
     * Then it adds them to list of raid users with their role
     * @param id The id of the user
     * @param name The name of the user
     * @param spec The specialization they are playing
     * @param role The role they will be playing in the raid
     * @param db_insert Whether or not the user should be inserted. This is false when the roles are loaded from the database.
     * @return true if the user was added, false otherwise
     */
    public boolean addUser(String id, String name, String spec, String role, boolean db_insert, boolean update_message) {
        RaidUser user = new RaidUser(id, name, spec, role);

        if(db_insert) {
            try {
                RaidBot.getInstance().getDatabase().update("INSERT INTO `raidUsers` (`userId`, `username`, `spec`, `role`, `raidId`)" +
                        " VALUES (?,?,?,?,?)", new String[]{
                        id,
                        name,
                        spec,
                        role,
                        this.messageId
                });
            } catch (SQLException e) {
                return false;
            }
        }

        userToRole.put(user, role);
        usersToFlexRoles.computeIfAbsent(user, k -> new ArrayList<>());

        if(update_message) {
            updateMessage();
        }
        return true;
    }

    /**
     * Add a user to a flex role in this raid.
     * This first creates the user and attempts to insert it into the database, if needed
     * Then it adds them to list of raid users' flex roles with their flex role
     * @param id The id of the user
     * @param name The name of the user
     * @param spec The specialization they are playing
     * @param role The flex role they will be playing in the raid
     * @param db_insert Whether or not the user should be inserted. This is false when the roles are loaded from the database.
     * @return true if the user was added, false otherwise
     */
    public boolean addUserFlexRole(String id, String name, String spec, String role, boolean db_insert, boolean update_message) {
        RaidUser user = getUserByName(name);
        FlexRole frole = new FlexRole(spec, role);

        if(db_insert) {
            try {
                RaidBot.getInstance().getDatabase().update("INSERT INTO `raidUsersFlexRoles` (`userId`, `username`, `spec`, `role`, `raidId`)" +
                        " VALUES (?,?,?,?,?)", new String[] {
                        id,
                        name,
                        spec,
                        role,
                        this.messageId
                });
            } catch (Exception e) {
                return false;
            }
        }

        if(usersToFlexRoles.get(user) == null) {
            usersToFlexRoles.put(user, new ArrayList<>());
        }

        usersToFlexRoles.get(user).add(frole);
        if(update_message) {
            updateMessage();
        }
        return true;
    }

    /**
     * Check if a specific user is in this raid
     * @param id The id of the user
     * @return True if they are in the raid, false otherwise
     */
    public boolean isUserInRaid(String id) {
        for(Map.Entry<RaidUser, String> entry : userToRole.entrySet()) {
            if(entry.getKey().getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove a user from this raid. This also updates the database to remove them from the raid and any flex roles they are in
     * @param id The user's id
     */
    public void removeUser(String id) {
        Iterator<Map.Entry<RaidUser, String>> users = userToRole.entrySet().iterator();
        while (users.hasNext()) {
            Map.Entry<RaidUser, String> user = users.next();
            if(user.getKey().getId().equalsIgnoreCase(id)) {
                users.remove();
                usersToFlexRoles.remove(user.getKey());
            }
        }

        try {
            RaidBot.getInstance().getDatabase().update("DELETE FROM `raidUsers` WHERE `userId` = ? AND `raidId` = ?",
                    new String[] {id, getMessageId()});
            RaidBot.getInstance().getDatabase().update("DELETE FROM `raidUsersFlexRoles` WHERE `userId` = ? and `raidId` = ?",
                    new String[] {id, getMessageId()});
        } catch (SQLException e) {
            e.printStackTrace();
        }

        updateMessage();
    }

    /**
     * Update the embedded message for the raid
     */
    public void updateMessage() {
        MessageEmbed embed = buildEmbed();
        try {
            RaidBot.getInstance().getServer(getServerId()).getTextChannelById(getChannelId())
                    .editMessageById(getMessageId(), embed).queue();
        } catch (Exception e) {}
    }

    /**
     * Build the embedded message that shows the information about this raid
     * @return The embedded message representing this raid
     */
    private MessageEmbed buildEmbed() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(getName());
        builder.addBlankField(false);
        if (getRaidLeaderName() != null) {
            builder.addField("Leader: ", "**" + getRaidLeaderName() + "**", false);
        }
        builder.addBlankField(false);
        builder.addField("Date: ", getDate(), true);
        builder.addField("Time: ", getTime(), true);
        builder.addBlankField(false);
        builder.addField("Roles:", buildRolesText(), true);
        builder.addField("Flex Roles:", buildFlexRolesText(), true);
        builder.addBlankField(false);
        builder.addField("ID: ", messageId, false);

        return builder.build();
    }

    /**
     * Build the flex roles text, which includes a list of flex roles users are playing and their specs
     * @return The flex role text
     */
    private String buildFlexRolesText() {
        String text = "";
        for (Map.Entry<RaidUser, List<FlexRole>> flex : usersToFlexRoles.entrySet()) {
            for (FlexRole frole : flex.getValue()) {
                text += ("- " + flex.getKey().getName() + " (" + frole.spec + "/" + frole.role + ")\n");
            }
        }

        return text;
    }

    /**
     * Build the role text, which shows the roles users are playing in the raids
     * @return The role text
     */
    private String buildRolesText() {
        String text = "";
        for(RaidRole role : roles) {
            text += ("**" + role.name + " (" + role.amount + "):** \n");
            for(RaidUser user : getUsersInRole(role.name)) {
                text += "   - " + user.name + " (" + user.spec + ")\n";
            }
            text += "\n";
        }
        return text;
    }

    /**
     * Get a RaidUser in this raid by their name
     * @param name The user's name
     * @return The RaidUser if they are in this raid, null otherwise
     */
    public RaidUser getUserByName(String name) {
        for(RaidUser user : userToRole.keySet()) {
            if(user.name.equalsIgnoreCase(name)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Remove a user by their username
     * @param name The name of the user being removed
     */
    public void removeUserByName(String name) {
        String idToRemove = "";
        for(Map.Entry<RaidUser, String> entry : userToRole.entrySet()) {
            if(entry.getKey().name.equalsIgnoreCase(name)) {
                idToRemove = entry.getKey().id;
            }
        }

        removeUser(idToRemove);
    }

    /**
     * Get the number of flex roles a user has
     * @param id The id of the user
     * @return The number of flex roles that a user has
     */
    public int getUserNumFlexRoles(String id) {
        for (Map.Entry<RaidUser, List<FlexRole>> entry : usersToFlexRoles.entrySet()) {
            RaidUser user = entry.getKey();
            if (user.id.equalsIgnoreCase(id)) {
                return entry.getValue().size();
            }
        }

        return 0;
    }
}
