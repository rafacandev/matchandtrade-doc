package com.matchandtrade.doc.executable;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matchandtrade.WebserviceApplication;
import com.matchandtrade.doc.config.PropertiesLoader;


/**
 * Main executable class for <pre>matchandtrade-cod</pre>
 * by default properties are loaded from <pre>src/config/matchandtrade.properties</pre>
 */
public class DocMakerExecutable {
	
	private static final Logger logger = LoggerFactory.getLogger(DocMakerExecutable.class);

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
		} catch (IOException e) {
			logger.error("Not able to start Match And Trade web server. Exception message: {}", e.getMessage());
		}
		
		DocContentMaker docContentMaker = new DocContentMaker(PropertiesLoader.destinationFolder());
		try {
			logger.info("Making documentation.");
			docContentMaker.makeContent();
			logger.info("Document generation complete.");
			logger.info(docContentMaker.getReport());		
		} catch (Exception e) {
			logger.error("Error when making the documentation. Exception message: {}", e.getMessage(), e);
			logger.info(docContentMaker.getReport());
			System.exit(-1);
		} finally {
			if (PropertiesLoader.stopWebService()) {
				logger.info("Documentation generated successfully. Stopping web server. " +
						"If you want to keep the web server running use the property {}=false. " +
						"Example: mvn -D{}=false",
						PropertiesLoader.Key.STOP_WEBSERVER.getKey(),
						PropertiesLoader.Key.STOP_WEBSERVER.getKey());
				System.exit(0);
			}
		}
		logger.info("Running Match And Trade Web API with base URL: {}", PropertiesLoader.serverUrl());
	}

	private static void startMatchAndTradeWebServer() throws Throwable {
		String [] emptyArguments = {};
		WebserviceApplication.main(emptyArguments);
	}

}
