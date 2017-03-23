package com.github.rafasantos.matchandtrade.doc.executable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rafasantos.matchandtrade.doc.generator.OutputGenerator;
import com.github.rafasantos.matchandtrade.doc.generator.rest.RestAuthenticate;
import com.github.rafasantos.matchandtrade.doc.generator.rest.RestAuthentication;
import com.matchandtrade.WebserviceApplication;

public class MatchAndTradeDocGenerator {
	
	private static final Logger logger = LoggerFactory.getLogger(MatchAndTradeDocGenerator.class);

	public static void main(String[] arguments) {
		try {
			logger.info("Starting Match and Trade web server.");
			String destinationFolder = ArgumentBuilder.obtainDestinationFolder(arguments);
			String[] webArgumentsArray = ArgumentBuilder.buildWebServerArguments(arguments);
			startMatchAndTradeWebServer(webArgumentsArray);
			MatchAndTradeDocGenerator mainInstance = new MatchAndTradeDocGenerator();
			mainInstance.execute(destinationFolder);
			logger.info("Document generation complete.");
			System.exit(0);
		} catch (Exception e) {
			logger.error("Exection interrupted. Exception message: {}", e.getMessage(), e);
			System.exit(-1);
		} finally {
			System.exit(0);
		}
	}
	
	public void execute(String destinationFolder) throws IOException {
		// Generate README.md
		String readmeLocation = MatchAndTradeDocGenerator.class.getClassLoader().getResource("doc/README.md").getPath();
		File readmeFile = new File(readmeLocation);
		File readmeDestination = new File(destinationFolder + File.separator + "README.md");
		FileUtils.copyFile(readmeFile, readmeDestination);
		
		// TODO Scan all files instead of instantiate one by one manually
		List<OutputGenerator> generators = new ArrayList<OutputGenerator>();
		generators.add(new RestAuthenticate());
		generators.add(new RestAuthentication());
		
		for(OutputGenerator t : generators) {
			t.execute();
			String tOutput = t.getDocOutput();
			String tOutputLocation = t.getDocOutputLocation();
			File tOutputFile = new File(destinationFolder + File.separator + tOutputLocation);
			FileUtils.write(tOutputFile, tOutput, StandardCharsets.UTF_8);
		}
	}

	private static void startMatchAndTradeWebServer(String[] arguments) throws IOException {
		PropertiesProvider.buildAppProperties(arguments);
		WebserviceApplication.main(arguments);
	}

}
