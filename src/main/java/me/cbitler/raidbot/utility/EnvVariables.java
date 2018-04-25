package me.cbitler.raidbot.utility;

import java.io.*;
import java.util.HashMap;

/**
 * Class for loading variables from .env file
 * @author Christopher Bitler
 */
public class EnvVariables {
    private HashMap<String,String> variables = new HashMap<>();

    /**
     * Load variables from .env file
     * @throws IOException
     */
    public void loadFromEnvFile() throws IOException {
        File file = new File(".env");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while((line = reader.readLine()) != null) {
            String[] parts = line.split("=");
            if(parts.length >= 2) {
                String name = parts[0].trim();
                String value = parts[1].trim();
                variables.put(name, value);
            }
        }
    }

    /**
     * Get a variable that was set in the .env file
     * @param key The variable name to get the value of
     * @return The value of the variable
     */
    public String getValue(String key) {
        return variables.get(key);
    }
}
