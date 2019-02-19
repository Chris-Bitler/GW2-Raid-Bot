package me.cbitler.raidbot.utility;

import me.cbitler.raidbot.RaidBot;
import net.dv8tion.jda.core.entities.Emote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reactions {
    /**
     * List of reactions representing classes
     */
    static String[] specs = {
            "Dragonhunter", //547490713764364329
            "Firebrand", //547490713928204308
            "Herald", //547490714049708032
            "Renegade", //547490713584140323
            "Berserker", //547490714041450496
            "Spellbreaker", //547490713990987806
            "Scrapper", //547490714079068184
            "Holosmith", //547490713911296040
            "Druid", // 547490713894518792
            "Soulbeast", //547490714041319439
            "Daredevil", //547490713927942169
            "Deadeye", //547490714209222676
            "Weaver", //547490714007764994
            "Tempest", //547490714045644810
            "Chronomancer", //547490713567232021
            "Mirage", //547490714624458752
            "Reaper", //547490713642729478
            "Scourge" //547490713965690897
    };

    static Emote[] reactions = {
            getEmoji("547490713764364329"), // Dragonhunter
            getEmoji("547490713928204308"), // Firebrand
            getEmoji("547490714049708032"), // Herald
            getEmoji("547490713584140323"), // Renegade
            getEmoji("547490714041450496"), // Berserker
            getEmoji("547490713990987806"), // Spellbreaker
            getEmoji("547490714079068184"), // Scrapper
            getEmoji("547490713911296040"), // Holosmith
            getEmoji("547490713894518792"), // Druid
            getEmoji("547490714041319439"), // Soulbeast
            getEmoji("547490713927942169"), // Daredevil
            getEmoji("547490714209222676"), // Deadeye
            getEmoji("547490714007764994"), // Weaver
            getEmoji("547490714045644810"), // Tempest
            getEmoji("547490713567232021"), // Chronomancer
            getEmoji("547490714624458752"), // Mirage
            getEmoji("547490713642729478"), // Reaper
            getEmoji("547490713965690897"), // Scourge
            getEmoji("547490713818890266") // X_
    };

    /**
     * Get an emoji from it's emote ID via JDA
     * @param id The ID of the emoji
     * @return The emote object representing that emoji
     */
    private static Emote getEmoji(String id) {
        return RaidBot.getInstance().getJda().getEmoteById(id);
    }

    /**
     * Get the list of reaction names as a list
     * @return The list of reactions as a list
     */
    public static List<String> getSpecs() {
        return new ArrayList<>(Arrays.asList(specs));
    }

    /**
     * Get the list of emote objects
     * @return The emotes
     */
    public static List<Emote> getEmotes() {
        return new ArrayList<>(Arrays.asList(reactions));
    }


}
