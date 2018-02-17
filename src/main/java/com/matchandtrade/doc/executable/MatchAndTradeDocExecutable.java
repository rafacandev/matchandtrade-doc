package com.matchandtrade.doc.executable;

import java.io.Console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matchandtrade.doc.config.PropertiesLoader;


/**
 * Main executable class for <code>matchandtrade-doc</code>.
 * Expects an argument <code>-cf</code> indicating the configuration file location.
 * Usage:  java -jar matchandtrade-doc.jar -cf <CONFIGURATION_FILE_LOCATION>.
 */
public class MatchAndTradeDocExecutable {
	
	private static final Logger logger = LoggerFactory.getLogger(MatchAndTradeDocExecutable.class);

	public static void main(String[] arguments) throws Throwable {
		logger.info("Loading environment variables and JVM properties as system properties.");
		
		String configurationFilePath = obtainConfigurationFilePath(arguments);
		PropertiesLoader.loadPropertiesFromConfigFile(configurationFilePath);

		MatchAndTradeContentBuilder docContentMaker = new MatchAndTradeContentBuilder(PropertiesLoader.destinationFolder());
		try {
			logger.info("Making documentation.");
			docContentMaker.buildContent();
			logger.info("Document generation completed sucessfully.");
		} catch (Exception | Error e) {
			logger.error("Error when making the documentation. Exception message: {}", e.getMessage(), e);
			throw(e);
		}
	}

	private static String obtainConfigurationFilePath(String[] arguments) {
		for (int i=0; i<arguments.length; i++) {
			if ("-cf".equals(arguments[i])) {
				return arguments[i+1];
			}
			if ("-h".equals(arguments[i]) || "--help".equals(arguments[i])) {
				Console console = System.console();
				console.writer().println("Usage:  java -jar matchandtrade-doc.jar -cf <CONFIGURATION_FILE_LOCATION>.");
			}
		}
		throw new IllegalArgumentException("[-cf] Configuration file location is a mandatory argument.");
	}

}
