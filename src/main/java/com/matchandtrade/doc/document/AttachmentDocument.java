package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.clientapi.MatchAndTradeClient;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.AttachmentJson;


public class AttachmentDocument implements Document {

	private static final String POST_PLACEHOLDER = "POST_PLACEHOLDER";
	private static final String GET_PLACEHOLDER = "GET_PLACEHOLDER";

	private final MatchAndTradeClient clientApi;
	private String template;

	public AttachmentDocument() {
		clientApi = new MatchAndTradeClient();
		template = TemplateUtil.buildTemplate(contentFilePath());
	}

	@Override
	public String content() {
		SpecificationParser postParser = clientApi.createAttachment("image-landscape.png");
		template = TemplateUtil.replacePlaceholder(template, POST_PLACEHOLDER, postParser.asHtmlSnippet());
		AttachmentJson attachment = postParser.getResponse().as(AttachmentJson.class);

		SpecificationParser getParser = clientApi.findAttachment(attachment.getAttachmentId());
		template = TemplateUtil.replacePlaceholder(template, GET_PLACEHOLDER, getParser.asHtmlSnippet());

		template = TemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

	@Override
	public String contentFilePath() {
		return "attachments.html";
	}

}
