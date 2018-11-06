
package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.util.MatchAndTradeClient;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.doc.util.TemplateUtil;


public class AttachmentDocument implements Document {
	
	private static final String POST_PLACEHOLDER = "POST_PLACEHOLDER";

	private final MatchAndTradeClient clientApi;
	private String template;

	public AttachmentDocument() {
		clientApi = new MatchAndTradeClient();
		template = TemplateUtil.buildTemplate(contentFilePath());
	}

	@Override
	public String content() {
		String filePath = AttachmentDocument.class.getClassLoader().getResource("image-landscape.png").getFile();
		SpecificationParser parser = clientApi.createAttachment(filePath);
		template = TemplateUtil.replacePlaceholder(template, POST_PLACEHOLDER, parser.asHtmlSnippet());

		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

	@Override
	public String contentFilePath() {
		return "attachments.html";
	}

}
