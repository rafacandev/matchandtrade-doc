package com.matchandtrade.doc.executable;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matchandtrade.WebserviceApplication;


/**
 * If no arguments are provided, then it will attribute the following values:
 * <code>
 * -cf src/config/matchandtrade.properties --destination-folder target/doc-maker
 * </code>
 */
public class DocMakerExecutable {
	
	private static final Logger logger = LoggerFactory.getLogger(DocMakerExecutable.class);

	public static void main(String[] arguments) throws Throwable {
		// TODO Better arguments handling
		if (arguments.length == 0) {
			arguments = new String[4];
			arguments[0] = "-cf";
			arguments[1] = "src/config/matchandtrade.properties";
			arguments[2] = "--destination-folder";
			arguments[3] = "target/doc-maker";
		}
		logger.info("Starting Match and Trade web server.");
		String destinationFolder = ArgumentBuilder.obtainDestinationFolder(arguments);
		String[] webArgumentsArray = ArgumentBuilder.buildWebServerArguments(arguments);
		try {
			startMatchAndTradeWebServer(webArgumentsArray);
		} catch (IOException e) {
			logger.error("Not able to start Match And Trade web server. Exception message: {}", e.getMessage());
		}
		
		DocContentMaker docContentMaker = new DocContentMaker(destinationFolder);
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
			System.exit(0);
		}
	}

	private static void startMatchAndTradeWebServer(String[] arguments) throws Throwable {
		PropertiesProvider.buildAppProperties(arguments);
		WebserviceApplication.main(arguments);
	}

}
