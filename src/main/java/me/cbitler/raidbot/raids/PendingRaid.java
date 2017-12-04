package me.cbitler.raidbot.raids;

import java.util.ArrayList;
import java.util.List;

public class PendingRaid {
    String name, date, time, announcementChannel, serverId;
    List<RaidRole> rolesWithNumbers = new ArrayList<RaidRole>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAnnouncementChannel() {
        return announcementChannel;
    }

    public void setAnnouncementChannel(String announcementChannel) {
        this.announcementChannel = announcementChannel;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public List<RaidRole> getRolesWithNumbers() {
        return rolesWithNumbers;
    }
}
