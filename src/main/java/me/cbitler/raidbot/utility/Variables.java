package me.cbitler.raidbot.utility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class for loading variables from config.properties file
 * @author MiniClem
 */
public class Variables {
	private static Variables INSTANCE;
	private static Properties properties;
	private final static String PROPERTY_FILENAME = "configuration.properties";

	private Variables() throws IOException {
		properties = new Properties();
		loadFromPropertyFile();
	}

	public static Variables getINSTANCE() throws IOException {
		if(INSTANCE == null) {
			INSTANCE = new Variables();
		}   
		return INSTANCE;
	}

	private static void loadFromPropertyFile() throws IOException {
		InputStream inputStream = Variables.class.getClassLoader().getResourceAsStream(PROPERTY_FILENAME);

		if(inputStream != null){
			properties.load(inputStream);
		}else {
			throw new FileNotFoundException("Property file " + PROPERTY_FILENAME + " not found in this classpath");
		}
	}

	/**
	 * Key names from the property file
	 */
	public enum RaidBotProperty{
		DATABASE, DISCORD_TOKEN, RAIDAR_USERNAME, RAIDAR_PASSWORD, TEST
	}

	public String getStringProperty(String key){
		return properties.getProperty(key);
	}
}
