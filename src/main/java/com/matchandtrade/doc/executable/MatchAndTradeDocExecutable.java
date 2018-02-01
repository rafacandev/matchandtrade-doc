package com.matchandtrade.doc.executable;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matchandtrade.WebserviceApplication;
import com.matchandtrade.doc.config.PropertiesLoader;


/**
 * Main executable class for <pre>matchandtrade-cod</pre>
 * by default properties are loaded from <pre>src/config/matchandtrade.properties</pre>
 */
public class MatchAndTradeDocExecutable {
	
	private static final Logger logger = LoggerFactory.getLogger(MatchAndTradeDocExecutable.class);

	public static void main(String[] arguments) throws Throwable {
		logger.info("Loading environment variables and JVM properties as system properties.");
		PropertiesLoader.loadConfigurationProperties();
		if (PropertiesLoader.configFile() != null) {
			logger.info("Loading properties from {}", PropertiesLoader.configFile());
			PropertiesLoader.loadConfigurationProperties(PropertiesLoader.configFile());
		}

		logger.info("Starting Match and Trade web server.");
		try {
			startMatchAndTradeWebServer();
			logger.info("Running Match And Trade Web API with base URL: {}", PropertiesLoader.serverUrl());
		} catch (IOException e) {
			logger.error("Not able to start Match And Trade web server. Exception message: {}", e.getMessage());
		}
		
		MatchAndTradeContentBuilder docContentMaker = new MatchAndTradeContentBuilder(PropertiesLoader.destinationFolder());
		int exitCode = 0;
		try {
			logger.info("Making documentation.");
			docContentMaker.buildContent();
			logger.info("Document generation complete.");
		} catch (Exception | Error e) {
			logger.error("Error when making the documentation. Exception message: {}", e.getMessage(), e);
			exitCode = -1;
		} finally {
			if (PropertiesLoader.stopWebService()) {
				logger.info("Documentation generated successfully. Stopping web server. " +
						"If you want to keep the web server running use the property {}=false. " +
						"Example: mvn -D{}=false",
						PropertiesLoader.Key.STOP_WEBSERVER.getKey(),
						PropertiesLoader.Key.STOP_WEBSERVER.getKey());
				System.exit(exitCode);
			}
		}
	}

	private static void startMatchAndTradeWebServer() throws Throwable {
		String [] emptyArguments = {};
		WebserviceApplication.main(emptyArguments);
	}

}
