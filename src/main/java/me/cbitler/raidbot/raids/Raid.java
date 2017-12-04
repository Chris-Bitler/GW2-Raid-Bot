package me.cbitler.raidbot.raids;

import me.cbitler.raidbot.RaidBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.sql.SQLException;
import java.util.*;

public class Raid {
    String messageId, name, date,time, serverId, channelId;
    List<RaidRole> roles = new ArrayList<RaidRole>();
    HashMap<RaidUser, String> userToRole = new HashMap<RaidUser, String>();

    public Raid(String messageId, String serverId, String channelId, String name, String date, String time) {
        this.messageId = messageId;
        this.serverId = serverId;
        this.channelId = channelId;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getServerId() { return serverId; }

    public String getChannelId() {
        return channelId;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public List<RaidRole> getRoles() {
        return roles;
    }

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

    private RaidRole getRole(String role) {
        for(RaidRole r : roles) {
            if(r.getName().equalsIgnoreCase(role)) {
                return r;
            }
        }

        return null;
    }

    private int getUserNumberInRole(String role) {
        int inRole = 0;
        for(Map.Entry<RaidUser, String> entry : userToRole.entrySet()) {
            if(entry.getValue().equalsIgnoreCase(role)) inRole+=1;
        }

        return inRole;
    }

    public List<RaidUser> getUsersInRole(String role) {
        List<RaidUser> users = new ArrayList<>();
        for(Map.Entry<RaidUser, String> entry : userToRole.entrySet()) {
            if(entry.getValue().equalsIgnoreCase(role)) {
                users.add(entry.getKey());
            }
        }

        return users;
    }

    public boolean addUser(String id, String name, String spec, String role, boolean db_insert) {
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
        updateMessage();
        return true;
    }

    public boolean isUserInRaid(String id) {
        for(Map.Entry<RaidUser, String> entry : userToRole.entrySet()) {
            if(entry.getKey().getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    public void removeUser(String id) {
        Iterator<Map.Entry<RaidUser, String>> users = userToRole.entrySet().iterator();
        while(users.hasNext()) {
            Map.Entry<RaidUser, String> user = users.next();
            if(user.getKey().getId().equalsIgnoreCase(id)) {
                users.remove();
            }
        }

        try {
            RaidBot.getInstance().getDatabase().update("DELETE FROM `raidUsers` WHERE `userId` = ? AND `raidId` = ?",
                    new String[] {id, getMessageId()});
        } catch (SQLException e) {
            e.printStackTrace();
        }

        updateMessage();
    }

    private void updateMessage() {
        MessageEmbed embed = buildEmbed();
        RaidBot.getInstance().getServer(getServerId()).getTextChannelById(getChannelId())
                .editMessageById(getMessageId(), embed).queue();
    }
    private MessageEmbed buildEmbed() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(getName());
        builder.addBlankField(false);
        builder.addField("Date: ", getDate(), true);
        builder.addField("Time: ", getTime(), true);
        builder.addField("Roles:", buildRolesText(), false);
        builder.addBlankField(false);
        builder.addField("ID: ", messageId, false);

        return builder.build();
    }

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

    public RaidUser getUserByName(String name) {
        for(RaidUser user : userToRole.keySet()) {
            if(user.name.equalsIgnoreCase(name)) {
                return user;
            }
        }
        return null;
    }

    public void removeUserByName(String name) {
        String idToRemove = "";
        for(Map.Entry<RaidUser, String> entry : userToRole.entrySet()) {
            if(entry.getKey().name.equalsIgnoreCase(name)) {
                idToRemove = entry.getKey().id;
            }
        }

        removeUser(idToRemove);
    }
}
