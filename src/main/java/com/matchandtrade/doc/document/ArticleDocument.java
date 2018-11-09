package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.clientapi.MatchAndTradeClient;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.ArticleJson;


public class ArticleDocument implements Document {
	
	private static final String ARTICLES_POST_PLACEHOLDER = "ARTICLES_POST_PLACEHOLDER";
	private static final String ARTICLES_PUT_PLACEHOLDER = "ARTICLES_PUT_PLACEHOLDER";
	private static final String ARTICLES_GET_PLACEHOLDER = "ARTICLES_GET_PLACEHOLDER";
	private static final String ARTICLES_GET_ALL_PLACEHOLDER = "ARTICLES_GET_ALL_PLACEHOLDER";
	private static final String ARTICLES_DELETE_PLACEHOLDER = "ARTICLES_DELETE_PLACEHOLDER";

	private final MatchAndTradeClient clientApi;
	private String template;

	public ArticleDocument() {
		template = TemplateUtil.buildTemplate(contentFilePath());
		clientApi = new MatchAndTradeClient();
	}

	@Override
	public String content() {
		template = TemplateUtil.buildTemplate(contentFilePath());

		// ARTICLES_POST_PLACEHOLDER
		ArticleJson article = new ArticleJson();
		article.setName("Pandemic Legacy: Season 1");
		article.setDescription("In mint condition");

		SpecificationParser postParser = clientApi.create(article);
		template = TemplateUtil.replacePlaceholder(template, ARTICLES_POST_PLACEHOLDER, postParser.asHtmlSnippet());
		article = postParser.getResponse().as(ArticleJson.class);

		// ARTICLES_PUT_PLACEHOLDER
		article.setName(article.getName() + " Updated");
		SpecificationParser putParser = clientApi.update(article);
		template = TemplateUtil.replacePlaceholder(template, ARTICLES_PUT_PLACEHOLDER, putParser.asHtmlSnippet());

		// ARTICLES_GET_PLACEHOLDER
		SpecificationParser getParser = MatchAndTradeClient.findArticle(article.getArticleId());
		template = TemplateUtil.replacePlaceholder(template, ARTICLES_GET_PLACEHOLDER, getParser.asHtmlSnippet());

		// ARTICLES_GET_ALL_PLACEHOLDER
		SpecificationParser getAllParser = MatchAndTradeClient.findArticles();
		template = TemplateUtil.replacePlaceholder(template, ARTICLES_GET_ALL_PLACEHOLDER, getAllParser.asHtmlSnippet());

		// ARTICLES_DELETE_PLACEHOLDER
		SpecificationParser deleteParser = clientApi.deleteArticle(article.getArticleId());
		template = TemplateUtil.replacePlaceholder(template, ARTICLES_DELETE_PLACEHOLDER, deleteParser.asHtmlSnippet());

		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

	@Override
	public String contentFilePath() {
		return "articles.html";
	}

}
