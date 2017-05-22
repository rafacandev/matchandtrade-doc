package com.matchandtrade.doc.executable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matchandtrade.doc.maker.DevelopmentGuide;
import com.matchandtrade.doc.maker.OutputMaker;
import com.matchandtrade.doc.maker.ReadmeMaker;
import com.matchandtrade.doc.maker.RestGuideMaker;
import com.matchandtrade.doc.maker.RestUseCaseMaker;
import com.matchandtrade.doc.maker.rest.RestAuthenticateMaker;
import com.matchandtrade.doc.maker.rest.RestAuthenticationMaker;
import com.matchandtrade.doc.maker.rest.RestItemMaker;
import com.matchandtrade.doc.maker.rest.RestTradeMembershipMaker;
import com.matchandtrade.doc.maker.rest.RestTradeMaker;
import com.matchandtrade.doc.maker.rest.RestUserMaker;
import com.matchandtrade.exception.DocMakerException;

public class DocContentMaker {
	
	private final Logger logger = LoggerFactory.getLogger(DocContentMaker.class);
	
	private String destinationFolder;
	private StringBuilder report = new StringBuilder();
	
	public DocContentMaker(String destinationFolder) {
		this.destinationFolder = destinationFolder;
	}
	
	public String getReport() {
		return report.toString();
	}
	
	public void makeContent() {
		// TODO Scan all files instead of instantiate one by one manually
		List<OutputMaker> docMakers = new ArrayList<OutputMaker>();
		docMakers.add(new ReadmeMaker());
		docMakers.add(new DevelopmentGuide());
		docMakers.add(new RestGuideMaker());
		docMakers.add(new RestUseCaseMaker());
		docMakers.add(new RestAuthenticateMaker());
		docMakers.add(new RestAuthenticationMaker());
		docMakers.add(new RestTradeMaker());
		docMakers.add(new RestUserMaker());
		docMakers.add(new RestTradeMembershipMaker());
		docMakers.add(new RestItemMaker());

		HtmlParser htmlParser = new HtmlParser();
		try {
			report.append("\n==== DocContentMaker Report ============\n");
			
			for(OutputMaker t : docMakers) {
				logger.info("Making content for {} with template doc located at {}.", t.getClass().getName(), t.getDocLocation());
				report.append(t.getClass().getSimpleName()+": ");
				// Generate Markdown documents
				String docContent = t.buildDocContent();
				String docLocation = t.getDocLocation();
				File docFile = new File(destinationFolder + File.separator + docLocation);
				FileUtils.write(docFile, docContent, StandardCharsets.UTF_8);
				// Also generate HTML files derived from MarkDown files for easier visualization
				File htmlFile = HtmlParser.optainHtmlFile(docFile);
				String docContentAsHtml = htmlParser.parseMarkDownToHTML(docContent);
				String styleHead = HtmlParser.getGitHubStyleHead();
				String styleTail = HtmlParser.getGitHubStyleTail();
				String htmlContetn = styleHead + docContentAsHtml + styleTail;
				FileUtils.write(htmlFile, htmlContetn, StandardCharsets.UTF_8);
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
