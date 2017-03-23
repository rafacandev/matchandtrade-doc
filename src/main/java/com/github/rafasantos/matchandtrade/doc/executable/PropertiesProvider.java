package com.github.rafasantos.matchandtrade.doc.executable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesProvider {

	private static Properties appProperties = new Properties();

	public static void buildAppProperties(String[] arguments) throws IOException {
		String configFilePath = null;
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i].equals("-cf")) {
				configFilePath = arguments[i + 1];
			}
		}
		File configFile = new File(configFilePath);
		if (!configFile.exists()) {
			throw new IOException("File does not exist: " + configFile.getAbsolutePath());
		} else if (configFile.isDirectory()) {
			throw new IOException("The path provided is a directory instead of a valid file: " + configFile.getAbsolutePath());
		} else if (!configFile.isFile()) {
			throw new IOException("The path provided is not a file: " + configFile.getAbsolutePath());
		} else {
			appProperties.load(new FileInputStream(configFile));
		}
	}

	public static String getServerUrl() {
		return "http://localhost:" + appProperties.getProperty("server.port");
	}
	
}
