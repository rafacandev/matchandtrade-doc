package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.clientapi.MatchAndTradeClient;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.ArticleJson;

public class ArticleAttachmentDocument implements Document {
	private static final String GET_ALL_PLACEHOLDER = "GET_ALL_PLACEHOLDER";
	private static final String PUT_PLACEHOLDER = "POST_PLACEHOLDER";

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

		SpecificationParser putParser = clientApi.createArticleAttachment(article.getArticleId(), "image-landscape.png");
		template = TemplateUtil.replacePlaceholder(template, PUT_PLACEHOLDER, putParser.asHtmlSnippet());

		SpecificationParser getParser = clientApi.findAttachmentsByArticleId(article.getArticleId());
		template = TemplateUtil.replacePlaceholder(template, GET_ALL_PLACEHOLDER, getParser.asHtmlSnippet());

		// TODO: Delete

		return TemplateUtil.appendHeaderAndFooter(template);
	}

	@Override
	public String contentFilePath() {
		return "article-attachments.html";
	}
}
