package com.github.rafasantos.matchandtrade.doc.executable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rafasantos.matchandtrade.doc.maker.OutputMaker;
import com.github.rafasantos.matchandtrade.doc.maker.rest.RestAuthenticateMaker;
import com.github.rafasantos.matchandtrade.doc.maker.rest.RestAuthenticationMaker;
import com.matchandtrade.WebserviceApplication;

public class DocMaker {
	
	private static final Logger logger = LoggerFactory.getLogger(DocMaker.class);

	public static void main(String[] arguments) {
		try {
			logger.info("Starting Match and Trade web server.");
			String destinationFolder = ArgumentBuilder.obtainDestinationFolder(arguments);
			String[] webArgumentsArray = ArgumentBuilder.buildWebServerArguments(arguments);
			startMatchAndTradeWebServer(webArgumentsArray);
			DocMaker mainInstance = new DocMaker();
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
		String readmeLocation = DocMaker.class.getClassLoader().getResource("doc/README.md").getPath();
		File readmeFile = new File(readmeLocation);
		File readmeDestination = new File(destinationFolder + File.separator + "README.md");
		FileUtils.copyFile(readmeFile, readmeDestination);
		
		// TODO Scan all files instead of instantiate one by one manually
		List<OutputMaker> docMakers = new ArrayList<OutputMaker>();
		docMakers.add(new RestAuthenticateMaker());
		docMakers.add(new RestAuthenticationMaker());
		
		for(OutputMaker t : docMakers) {
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
