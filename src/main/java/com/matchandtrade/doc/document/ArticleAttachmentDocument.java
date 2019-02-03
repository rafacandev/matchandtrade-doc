package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.clientapi.MatchAndTradeClient;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.ArticleJson;
import com.matchandtrade.rest.v1.json.AttachmentJson;


public class ArticleAttachmentDocument implements Document {
	
	private static final String DELETE_PLACEHOLDER = "DELETE_PLACEHOLDER";
	private static final String PUT_PLACEHOLDER = "PUT_PLACEHOLDER";

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
		AttachmentJson attachment = clientApi.createAttachment("image-landscape.png").getResponse().as(AttachmentJson.class);

		SpecificationParser putParser = clientApi.createArticleAttachment(article.getArticleId(), attachment.getAttachmentId());
		template = TemplateUtil.replacePlaceholder(template, PUT_PLACEHOLDER, putParser.asHtmlSnippet());
		//TODO
//		SpecificationParser deleteParser = clientApi.deleteArticleAttachment(article.getArticleId(), attachment.getAttachmentId());
//		template = TemplateUtil.replacePlaceholder(template, DELETE_PLACEHOLDER, deleteParser.asHtmlSnippet());

		return TemplateUtil.appendHeaderAndFooter(template);
	}

	@Override
	public String contentFilePath() {
		return "article-attachments.html";
	}

}
