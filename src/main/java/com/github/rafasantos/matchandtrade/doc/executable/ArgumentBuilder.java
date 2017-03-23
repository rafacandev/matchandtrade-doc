package com.github.rafasantos.matchandtrade.doc.executable;

import java.util.ArrayList;
import java.util.List;

public class ArgumentBuilder {

	public static String[] buildWebServerArguments(String[] arguments) {
		List<String> webArgumentsList = new ArrayList<>();
		for (int i=0; i < arguments.length; i++) {
			if (arguments[i].equals("-cf")) {
				webArgumentsList.add("-cf");
				webArgumentsList.add(arguments[i+1]);
			}
		}
		String[] webArgumentsArray = new String[webArgumentsList.size()];
		webArgumentsArray = webArgumentsList.toArray(webArgumentsArray);
		return webArgumentsArray;
	}
	
	public static String obtainDestinationFolder(String[] arguments) {
		String destinationFolder = null;
		for (int i=0; i < arguments.length; i++) {
			if (arguments[i].equals("--destination-folder")) {
				destinationFolder = arguments[i+1];
			}
		}
		return destinationFolder;
	}
	
}
