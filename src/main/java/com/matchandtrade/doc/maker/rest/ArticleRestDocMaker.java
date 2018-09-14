package com.matchandtrade.doc.maker.rest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Date;

import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.github.rafasantos.restdocmaker.util.JsonUtil;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.rest.v1.json.ArticleJson;
import com.matchandtrade.rest.v1.json.TradeJson;

import io.restassured.http.ContentType;
import io.restassured.http.Method;


public class ArticleRestDocMaker implements RestDocMaker {
	
	private static final String ARTICLES_POST_PLACEHOLDER = "ARTICLES_POST_PLACEHOLDER";
	private static final String ARTICLES_PUT_PLACEHOLDER = "ARTICLES_PUT_PLACEHOLDER";
	private static final String ARTICLES_GET_PLACEHOLDER = "ARTICLES_GET_PLACEHOLDER";
	private static final String ARTICLES_SEARCH_PLACEHOLDER = "ARTICLES_SEARCH_PLACEHOLDER";
	private static final String ARTICLES_GET_ALL_PLACEHOLDER = "ARTICLES_GET_ALL_PLACEHOLDER";
	private static final String ARTICLES_DELETE_PLACEHOLDER = "ARTICLES_DELETE_PLACEHOLDER";

	@Override
	public String contentFilePath() {
		return "articles.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());
		MatchAndTradeApiFacade apiFacade = new MatchAndTradeApiFacade();
		SnippetFactory snippetFactory = new SnippetFactory(ContentType.JSON, MatchAndTradeRestUtil.getLastAuthorizationHeader());
		
		// Create a trade membership
		TradeJson trade = apiFacade.createTrade("Board games in Montreal - " + new Date().getTime() + this.hashCode());
		Integer membershipId = apiFacade.findMembershipByUserIdAndTradeId(MatchAndTradeRestUtil.getLastAuthenticatedUserId(), trade.getTradeId()).getMembershipId();
		
		// ARTICLES_POST_PLACEHOLDER
		ArticleJson article = new ArticleJson();
		article.setName("Pandemic Legacy: Season 1");
		article.setDescription("In mint condition");
		Snippet postSnippet = snippetFactory.makeSnippet(Method.POST, article, MatchAndTradeRestUtil.articlesUrl(membershipId) + "/");
		postSnippet.getResponse().then().statusCode(201).and().body("name", equalTo(article.getName()));
		template = TemplateUtil.replacePlaceholder(template, ARTICLES_POST_PLACEHOLDER, postSnippet.asHtml());
		article = JsonUtil.fromResponse(postSnippet.getResponse(), ArticleJson.class);
		
		// ARTICLES_PUT_PLACEHOLDER
		Integer articleId = article.getArticleId();
		article.setArticleId(null); // Set as null because we do not want to display in the documentation
		article.setLinks(null); // Set as null because we do not want to display in the documentation
		article.setName(article.getName() + " After PUT");
		Snippet putSnippet = snippetFactory.makeSnippet(Method.PUT, article, MatchAndTradeRestUtil.articlesUrl(membershipId, articleId));
		putSnippet.getResponse().then().statusCode(200).and().body("name", equalTo(article.getName()));
		template = TemplateUtil.replacePlaceholder(template, ARTICLES_PUT_PLACEHOLDER, putSnippet.asHtml());

		// ARTICLES_GET_PLACEHOLDER
		Snippet getSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.articlesUrl(membershipId, articleId));
		getSnippet.getResponse().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, ARTICLES_GET_PLACEHOLDER, getSnippet.asHtml());
		
		// ARTICLES_GET_ALL_PLACEHOLDER
		Snippet getAllSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.articlesUrl(membershipId) + "/");
		getAllSnippet.getResponse().then().statusCode(200).and().header("X-Pagination-Total-Count", notNullValue());
		template = TemplateUtil.replacePlaceholder(template, ARTICLES_GET_ALL_PLACEHOLDER, getAllSnippet.asHtml());

		// ARTICLES_SEARCH_PLACEHOLDER
		Snippet searchSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.articlesUrl(membershipId));
		searchSnippet.getResponse().then().statusCode(200).and().header("X-Pagination-Total-Count", "1");
		template = TemplateUtil.replacePlaceholder(template, ARTICLES_SEARCH_PLACEHOLDER, searchSnippet.asHtml());
		
		// ARTICLES_DELETE_PLACEHOLDER
		Snippet deleteSnippet = snippetFactory.makeSnippet(Method.DELETE, MatchAndTradeRestUtil.articlesUrl(membershipId, articleId));
		deleteSnippet.getResponse().then().statusCode(204);
		template = TemplateUtil.replacePlaceholder(template, ARTICLES_DELETE_PLACEHOLDER, deleteSnippet.asHtml());

		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

}
