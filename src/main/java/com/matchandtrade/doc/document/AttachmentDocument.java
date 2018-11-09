
package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.clientapi.MatchAndTradeClient;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.AttachmentJson;


public class AttachmentDocument implements Document {
	
	private static final String POST_PLACEHOLDER = "POST_PLACEHOLDER";

	private final MatchAndTradeClient clientApi;
	private String template;
	public static final String ATTACHMENT_FILE_PATH = "image-landscape.png";

	public AttachmentDocument() {
		clientApi = new MatchAndTradeClient();
		template = TemplateUtil.buildTemplate(contentFilePath());
	}

	@Override
	public String content() {
		SpecificationParser postParser = clientApi.createAttachment(ATTACHMENT_FILE_PATH);
		template = TemplateUtil.replacePlaceholder(template, POST_PLACEHOLDER, postParser.asHtmlSnippet());
		AttachmentJson attachment = postParser.getResponse().as(AttachmentJson.class);

		// TODO: DELETE_PLACEHOLDER

		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

	@Override
	public String contentFilePath() {
		return "attachments.html";
	}

}
