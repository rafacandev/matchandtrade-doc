package com.matchandtrade.doc.executable;

import com.matchandtrade.doc.maker.DocumentContent;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public class ContentGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContentGenerator.class);
	// TODO remove these hardcoded string values, move to a config
	private String destinationFolderRootPath = "rest-api-doc";
	private String cssFilePath = "/templates/css/rest-api-doc.css";
	private String jsFilePath = "/templates/js/rest-api-doc.js";


	public ContentGenerator(String destinationFolderRootPath, String cssFilePath) {
		this.destinationFolderRootPath = destinationFolderRootPath;
		this.cssFilePath = cssFilePath;
	}

	public void generate(DocumentContent content) {

		InputStream restDocMakerCss = ContentGenerator.class.getResourceAsStream(cssFilePath);
		File cssDestinationFolder = new File(destinationFolderRootPath + File.separatorChar + "css" + File.separatorChar + "rest-api-doc.css");

		InputStream restDocMakerJs = ContentGenerator.class.getResourceAsStream(jsFilePath);
		File jsDestinationFolder = new File(destinationFolderRootPath + File.separatorChar + "js" + File.separatorChar + "rest-api-doc.js");

		try {
			FileUtils.copyInputStreamToFile(restDocMakerCss, cssDestinationFolder);
			FileUtils.copyInputStreamToFile(restDocMakerJs, jsDestinationFolder);
		} catch (IOException e) {
			LOGGER.error("Unable to copy file. Exception message {}.", e.getMessage(), e);
		}

		try {
			File contentFile = new File(destinationFolderRootPath + File.separatorChar + content.contentFilePath());
			FileUtils.write(contentFile, content.content(), StandardCharsets.UTF_8);
			LOGGER.info("Documentation file generated: {}", contentFile.getPath());
		} catch (IOException e) {
			LOGGER.error("Unable to write documentation file for content: {}. Exception message: {}", content.getClass(), e.getMessage());
			throw new UncheckedIOException(e);
		} catch (Exception | Error e) {
			LOGGER.error("Unable to generate documentation for content: {}. Exception message {}: ", content.getClass(), e.getMessage());
			throw e;
		}
	}

}
