package me.cbitler.raidbot.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reactions {
    static String[] reactions = {
            "Dragonhunter",
            "Firebrand",
            "Herald",
            "Renegade",
            "Beserker",
            "Spellbreaker",
            "Scrapper",
            "Holosmith",
            "Druid",
            "Soulbeast",
            "Daredevil",
            "Deadeye",
            "Weaver",
            "Tempest",
            "Chronomancer",
            "Mirage",
            "Reaper",
            "Scourge"
    };

    public static List<String> getEmojis() {
        ArrayList<String> emojis = new ArrayList<String>(Arrays.asList(reactions));
        return emojis;
    }


}
