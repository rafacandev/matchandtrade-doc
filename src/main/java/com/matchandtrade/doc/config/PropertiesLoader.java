package com.matchandtrade.doc.config;

public class PropertiesLoader {
	public static void loadConfigurationProperties() {
		for (int i = 0; i < PropertyKey.values().length; i++) {
			if (System.getProperty(PropertyKey.values()[i].getKey()) == null) {
				System.setProperty(PropertyKey.values()[i].getKey(), PropertyKey.values()[i].getDefaultValue());
			}
		}
	}
	
	public static String getSystemProperty(PropertyKey k) {
		return System.getProperty(k.getKey());
	}
	
}