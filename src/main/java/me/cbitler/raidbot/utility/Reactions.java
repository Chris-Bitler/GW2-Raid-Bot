package me.cbitler.raidbot.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reactions {
    /**
     * List of reactions representing classes
     */
    static String[] reactions = {
            "Dragonhunter",
            "Firebrand",
            "Herald",
            "Renegade",
            "Berserker",
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

    /**
     * Get the list of reactions as a list
     * @return The list of reactions as a list
     */
    public static List<String> getEmojis() {
        ArrayList<String> emojis = new ArrayList<String>(Arrays.asList(reactions));
        return emojis;
    }


}
