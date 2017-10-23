package com.matchandtrade.doc.config;

public enum PropertyKey {
	DOC_DESTINATION_FOLDER("matchandtrade.doc.destination.folder","target/doc-maker"),
	STOP_WEBSERVER("matchandtrade.doc.stop.webserver","true");

	private final String defaultValue;
	private final String key;

	PropertyKey(final String key, final String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }
	public String getKey() {
		return key;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public static PropertyKey getKey(String s) {
		PropertyKey[] k = PropertyKey.values();
		for (int i = 0; i < k.length; i++) {
			PropertyKey key = k[i];
			if (key.getKey().equals(s)) {
				return key;
			}
		}
		return null;
	}
	@Override
	public String toString() {
		return key + "=" + defaultValue;
	}
}
