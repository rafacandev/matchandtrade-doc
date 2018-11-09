package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.clientapi.MatchAndTradeClient;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.ArticleJson;
import com.matchandtrade.rest.v1.json.AttachmentJson;


public class ArticleAttachmentDocument implements Document {
	
	private static final String POST_PLACEHOLDER = "POST_PLACEHOLDER";
	// TODO
	private static final String GET_ALL_PLACEHOLDER = "GET_ALL_PLACEHOLDER";
	private static final String GET_PLACEHOLDER = "GET_PLACEHOLDER";
	private static final String DELETE_PLACEHOLDER = "DELETE_PLACEHOLDER";

	private final MatchAndTradeClient clientApi;
	private String template;

	public ArticleAttachmentDocument() {
		clientApi = new MatchAndTradeClient();
		template = TemplateUtil.buildTemplate(contentFilePath());
	}

	@Override
	public String content() {
		ArticleJson article = new ArticleJson();
		article.setName("Article");
		article = clientApi.create(article).getResponse().as(ArticleJson.class);

		AttachmentJson attachment = clientApi
			.createAttachment(AttachmentDocument.ATTACHMENT_FILE_PATH)
			.getResponse()
			.as(AttachmentJson.class);

		SpecificationParser postParser = clientApi.create(article.getArticleId(), attachment.getAttachmentId());
		template = TemplateUtil.replacePlaceholder(template, POST_PLACEHOLDER, postParser.asHtmlSnippet());

		SpecificationParser deleteParser = clientApi.deleteArticleAttachment(article.getArticleId(), attachment.getAttachmentId());
		template = TemplateUtil.replacePlaceholder(template, DELETE_PLACEHOLDER, deleteParser.asHtmlSnippet());

		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

	@Override
	public String contentFilePath() {
		return "article-attachments.html";
	}

}
