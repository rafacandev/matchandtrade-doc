package com.matchandtrade.doc.maker.rest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Date;

import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeJson.State;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;


public class TradeRestDocMaker implements RestDocMaker {
	
	private static final String TRADES_POST_PLACEHOLDER = "TRADES_POST_PLACEHOLDER";
	private static final String TRADES_PUT_PLACEHOLDER = "TRADES_PUT_PLACEHOLDER";	
	private static final String TRADES_GET_PLACEHOLDER = "TRADES_GET_PLACEHOLDER";
	private static final String TRADES_DELETE_PLACEHOLDER = "TRADES_DELETE_PLACEHOLDER";
	private static final String TRADES_SEARCH_PLACEHOLDER = "TRADES_SEARCH_PLACEHOLDER";
	private static final String TRADES_GET_ALL_PLACEHOLDER = "TRADES_GET_ALL_PLACEHOLDER";
	
	@Override
	public String contentFilePath() {
		return "trades.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());
		SnippetFactory snippetFactory = new SnippetFactory(ContentType.JSON, MatchAndTradeRestUtil.getLastAuthorizationHeader());
		
		// TRADES_POST_PLACEHOLDER
		TradeJson tradeJson = new TradeJson();
		String tradeName = "Board games location TBD - " + new Date().getTime() + this.hashCode();
		tradeJson.setName(tradeName);
		Snippet postSnippet = snippetFactory.makeSnippet(Method.POST, tradeJson, MatchAndTradeRestUtil.tradesUrl() + "/");
		postSnippet.getResponse().then().statusCode(201).and().body("", hasKey("tradeId"));
		template = TemplateUtil.replacePlaceholder(template, TRADES_POST_PLACEHOLDER, postSnippet.asHtml());
		tradeJson = postSnippet.getResponse().body().as(TradeJson.class);
		
		// TRADES_PUT_PLACEHOLDER
		Integer tradeId = tradeJson.getTradeId();
		tradeJson.setTradeId(null); // Set as null because we do not want to display in the documentation
		tradeJson.setLinks(null); // Set as null because we do not want to display in the documentation
		tradeName = "Board games in Toronto - " + new Date().getTime() + this.hashCode(); 
		tradeJson.setName(tradeName);
		tradeJson.setState(State.MATCHING_ITEMS);
		Snippet putSnippet = snippetFactory.makeSnippet(Method.PUT, tradeJson, MatchAndTradeRestUtil.tradesUrl(tradeId));
		putSnippet.getResponse().then().statusCode(200).and().body("name", equalTo(tradeJson.getName()));
		template = TemplateUtil.replacePlaceholder(template, TRADES_PUT_PLACEHOLDER, putSnippet.asHtml());
		
		// TRADES_GET_PLACEHOLDER
		Snippet getSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.tradesUrl(tradeId));
		getSnippet.getRequest().then().statusCode(200).and().body("tradeId", equalTo(tradeId));
		template = TemplateUtil.replacePlaceholder(template, TRADES_GET_PLACEHOLDER, getSnippet.asHtml());
		
		// TRADES_SEARCH_PLACEHOLDER
		RequestSpecification searchRequest = new RequestSpecBuilder()
				.addHeaders(MatchAndTradeRestUtil.getLastAuthorizationHeaderAsMap())
				.addQueryParam("_pageNumber", "1")
				.addQueryParam("_pageSize", "3")
				.build();
		Snippet searchSnippet = SnippetFactory.makeSnippet(Method.GET, searchRequest, MatchAndTradeRestUtil.tradesUrl()); 
		searchSnippet.getResponse().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, TRADES_SEARCH_PLACEHOLDER, searchSnippet.asHtml());

		// TRADES_GET_ALL_PLACEHOLDER
		Snippet getAllSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.tradesUrl());
		getAllSnippet.getResponse().then().statusCode(200).and().headers("X-Pagination-Total-Count", notNullValue());
		template = TemplateUtil.replacePlaceholder(template, TRADES_GET_ALL_PLACEHOLDER, getSnippet.asHtml());
		
		// TRADES_DELETE_PLACEHOLDER
		Snippet deleteSnippet = snippetFactory.makeSnippet(Method.DELETE, MatchAndTradeRestUtil.tradesUrl(tradeId));
		deleteSnippet.getResponse().then().statusCode(204);
		template = TemplateUtil.replacePlaceholder(template, TRADES_DELETE_PLACEHOLDER, deleteSnippet.asHtml());

		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

}
