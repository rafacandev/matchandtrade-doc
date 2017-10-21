package com.matchandtrade.doc.executable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matchandtrade.doc.maker.CssMaker;
import com.matchandtrade.doc.maker.DevelopmentGuide;
import com.matchandtrade.doc.maker.IndexMaker;
import com.matchandtrade.doc.maker.OutputMaker;
import com.matchandtrade.doc.maker.RestUseCaseMaker;
import com.matchandtrade.doc.maker.rest.RestAuthenticateMaker;
import com.matchandtrade.doc.maker.rest.RestAuthenticationMaker;
import com.matchandtrade.doc.maker.rest.RestItemMaker;
import com.matchandtrade.doc.maker.rest.RestTradeMaker;
import com.matchandtrade.doc.maker.rest.RestTradeMembershipMaker;
import com.matchandtrade.doc.maker.rest.RestUserMaker;
import com.matchandtrade.doc.maker.rest.RestWantItemMaker;
import com.matchandtrade.exception.DocMakerException;

public class DocContentMaker {
	
	private final Logger logger = LoggerFactory.getLogger(DocContentMaker.class);
	
	private String destinationFolder;
	private StringBuilder report = new StringBuilder();
	private final String HTML_HEADER = "<!DOCTYPE html>\n" + 
			"<html>\n" +
			"	<head>\n" +
			"		<meta charset='UTF-8'>\n" +
			"		<link rel='stylesheet' href='css/combined-style.css'>\n" +
			"		<link rel='stylesheet' href='" + CssMaker.CSS_LOCATION + "'>\n" +
			"		<title>Match and Trade - Documentation</title>\n" +
			"	</head>\n" +
			"<body>\n";
	private final String HTML_FOOTER = "</body>\n</html>\n";
	
	
	public DocContentMaker(String destinationFolder) {
		this.destinationFolder = destinationFolder;
	}
	
	public String getReport() {
		return report.toString();
	}
	
	public void makeContent() {
		// TODO Scan all files instead of instantiate one by one manually
		List<OutputMaker> docMakers = new ArrayList<OutputMaker>();
		docMakers.add(new CssMaker());
		docMakers.add(new DevelopmentGuide());
		docMakers.add(new RestUseCaseMaker());
		docMakers.add(new RestAuthenticateMaker());
		docMakers.add(new RestAuthenticationMaker());
		docMakers.add(new RestTradeMaker());
		docMakers.add(new RestUserMaker());
		docMakers.add(new RestTradeMembershipMaker());
		docMakers.add(new RestItemMaker());
		docMakers.add(new RestWantItemMaker());
		docMakers.add(new IndexMaker());
		try {
			report.append("\n==== DocContentMaker Report ============\n");
			for(OutputMaker t : docMakers) {
				logger.info("Making content for {} with template doc located at {}.", t.getClass().getName(), t.getDocLocation());
				report.append(t.getClass().getSimpleName()+": ");
				// Generate HTML documents
				String docContent = null;
				if (t.requiresHeaderAndFooter()) {
					docContent = HTML_HEADER + t.buildDocContent() + HTML_FOOTER;
				} else {
					docContent = t.buildDocContent();
				}
				String docLocation = t.getDocLocation();
				File docFile = new File(destinationFolder + File.separator + docLocation);
				FileUtils.write(docFile, docContent, StandardCharsets.UTF_8);
				report.append("Success.\n");
			}
		} catch (Exception e) {
			report.append("Failed.\n");
			throw new DocMakerException(e);
		} finally {
			report.append("========================================");
		}
	}
}
