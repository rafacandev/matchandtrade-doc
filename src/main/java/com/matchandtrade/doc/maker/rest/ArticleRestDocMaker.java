package com.matchandtrade.doc.maker.rest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.rest.v1.json.ArticleJson;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;


public class ArticleRestDocMaker implements RestDocMaker {
	
	private static final String ARTICLES_POST_PLACEHOLDER = "ARTICLES_POST_PLACEHOLDER";
	private static final String ARTICLES_PUT_PLACEHOLDER = "ARTICLES_PUT_PLACEHOLDER";
	private static final String ARTICLES_GET_PLACEHOLDER = "ARTICLES_GET_PLACEHOLDER";
	private static final String ARTICLES_GET_ALL_PLACEHOLDER = "ARTICLES_GET_ALL_PLACEHOLDER";
	private static final String ARTICLES_DELETE_PLACEHOLDER = "ARTICLES_DELETE_PLACEHOLDER";

	@Override
	public String contentFilePath() {
		return "articles.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());
		SnippetFactory snippetFactory = new SnippetFactory(ContentType.JSON, MatchAndTradeRestUtil.getLastAuthorizationHeader());
		
		// ARTICLES_POST_PLACEHOLDER
		ArticleJson article = new ArticleJson();
		article.setName("Pandemic Legacy: Season 1");
		article.setDescription("In mint condition");

		SpecificationParser postParser = buildPostParser(article);
		template = TemplateUtil.replacePlaceholder(template, ARTICLES_POST_PLACEHOLDER, postParser.asHtmlSnippet());

		// ARTICLES_PUT_PLACEHOLDER
		SpecificationParser putParser = buildPutParser(article);
		template = TemplateUtil.replacePlaceholder(template, ARTICLES_PUT_PLACEHOLDER, putParser.asHtmlSnippet());

		// ARTICLES_GET_PLACEHOLDER
		SpecificationParser getParser = buildGetSnippet(article.getArticleId());
		template = TemplateUtil.replacePlaceholder(template, ARTICLES_GET_PLACEHOLDER, getParser.asHtmlSnippet());

		// ARTICLES_GET_ALL_PLACEHOLDER
		SpecificationParser getAllParser = buildGetAllSnippet(snippetFactory);
		template = TemplateUtil.replacePlaceholder(template, ARTICLES_GET_ALL_PLACEHOLDER, getAllParser.asHtmlSnippet());

		// ARTICLES_DELETE_PLACEHOLDER
		SpecificationParser deleteParser = buildDeleteSnippet(article.getArticleId());
		template = TemplateUtil.replacePlaceholder(template, ARTICLES_DELETE_PLACEHOLDER, deleteParser.asHtmlSnippet());

		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

	private SpecificationParser buildDeleteSnippet(Integer articleId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
				.given()
				.filter(filter)
				.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
				.get(MatchAndTradeRestUtil.articlesUrl(articleId));
		parser.getResponse().then().statusCode(200);
		return parser;
	}

	private SpecificationParser buildGetAllSnippet(SnippetFactory snippetFactory) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
				.given()
				.filter(filter)
				.get(MatchAndTradeRestUtil.articlesUrl());
		parser.getResponse().then().statusCode(200);
		return parser;
	}

	private SpecificationParser buildGetSnippet(Integer articleId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
				.given()
				.filter(filter)
				.get(MatchAndTradeRestUtil.articlesUrl() + "/");
		parser.getResponse().then().statusCode(200);
		return parser;
	}

	private SpecificationParser buildPostParser(ArticleJson article) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
				.given()
				.filter(filter)
				.contentType(ContentType.JSON)
				.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
				.body(article)
				.post(MatchAndTradeRestUtil.articlesUrl() + "/");
		parser.getResponse().then().statusCode(201).and().body("name", equalTo(article.getName()));
		ArticleJson articleFromResponse = parser.getResponse().as(ArticleJson.class);
		article.setArticleId(articleFromResponse.getArticleId());
		return parser;
	}

	private SpecificationParser buildPutParser(ArticleJson article) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		Integer articleId = article.getArticleId();
		article.setArticleId(null);
		article.setLinks(null);
		article.setName(article.getName() + " After PUT");
		RestAssured
				.given()
				.filter(filter)
				.contentType(ContentType.JSON)
				.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
				.body(article)
				.put(MatchAndTradeRestUtil.articlesUrl(articleId));
		parser.getResponse().then().statusCode(200).and().body("name", equalTo(article.getName()));
		ArticleJson articleFromResponse = parser.getResponse().as(ArticleJson.class);
		article.setArticleId(articleFromResponse.getArticleId());
		article.setName(articleFromResponse.getName());
		return parser;
	}

}
