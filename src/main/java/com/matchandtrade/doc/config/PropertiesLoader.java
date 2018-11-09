package com.matchandtrade.doc.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

public class PropertiesLoader {


	public enum Key {
		CSS_FILE("matchandtrade.doc.css.file"),
		DESTINATION_FOLDER("matchandtrade.doc.destination.folder"),
		JS_FILE("matchandtrade.doc.js.file");

		private final String key;

		Key(final String key) {
	        this.key = key;
	    }
		@Override
		public String toString() {
			return key;
		}
	}

	public static void loadPropertiesFromConfigFile(String configFilePath) throws IOException {
		File configFile = new File(configFilePath);
		InputStream configFileAsInputStream = FileUtils.openInputStream(configFile);
		Properties configProperties = new Properties();
		configProperties.load(configFileAsInputStream);
		configProperties.entrySet().forEach( e ->
				System.setProperty(e.getKey().toString(), e.getValue().toString())
		);
	}

	public static String jsFile() {
		return System.getProperty(Key.JS_FILE.toString());
	}

	public static String cssFile() {
		return System.getProperty(Key.CSS_FILE.toString());
	}

	public static String serverUrl() {
		return "http://localhost:" + System.getProperty("server.port");
	}
	
	public static String destinationFolder() {
		return System.getProperty(Key.DESTINATION_FOLDER.toString());
	}
	
}