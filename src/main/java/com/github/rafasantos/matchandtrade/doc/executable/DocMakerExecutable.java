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
import com.github.rafasantos.matchandtrade.doc.maker.ReadmeMaker;
import com.github.rafasantos.matchandtrade.doc.maker.developmentguide.DevelopmentGuide;
import com.github.rafasantos.matchandtrade.doc.maker.rest.RestAuthenticateMaker;
import com.github.rafasantos.matchandtrade.doc.maker.rest.RestAuthenticationMaker;
import com.github.rafasantos.matchandtrade.doc.maker.rest.RestMaker;
import com.matchandtrade.WebserviceApplication;


/**
 * If no arguments are provided, then it will attribute the following values:
 * <code>
 * -cf src/config/matchandtrade.properties --destination-folder target/doc-maker
 * </code>
 */
public class DocMakerExecutable {
	
	private static final Logger logger = LoggerFactory.getLogger(DocMakerExecutable.class);

	public static void main(String[] arguments) {
		// TODO Better arguments handling
		try {
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
			startMatchAndTradeWebServer(webArgumentsArray);
			DocMakerExecutable mainInstance = new DocMakerExecutable();
			mainInstance.execute(destinationFolder);
			logger.info("Document generation complete.");
			System.exit(0);
		} catch (Exception e) {
			logger.error("Error when generating the documentation.", e);
			System.exit(-1);
		} finally {
			System.exit(0);
		}
	}
	
	public void execute(String destinationFolder) throws IOException {
		// TODO Scan all files instead of instantiate one by one manually
		List<OutputMaker> docMakers = new ArrayList<OutputMaker>();
		docMakers.add(new ReadmeMaker());
		docMakers.add(new DevelopmentGuide());
		docMakers.add(new RestMaker());
		docMakers.add(new RestAuthenticateMaker());
		docMakers.add(new RestAuthenticationMaker());

		for(OutputMaker t : docMakers) {
			String docContent = t.obtainDocContent();
			String docLocation = t.obtainDocLocation();
			File docFile = new File(destinationFolder + File.separator + docLocation);
			FileUtils.write(docFile, docContent, StandardCharsets.UTF_8);
		}
	}

	private static void startMatchAndTradeWebServer(String[] arguments) throws IOException {
		PropertiesProvider.buildAppProperties(arguments);
		WebserviceApplication.main(arguments);
	}

}
