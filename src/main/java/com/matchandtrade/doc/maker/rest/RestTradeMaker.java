package com.matchandtrade.doc.maker.rest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.*;

import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.matchandtrade.doc.maker.OutputMaker;
import com.matchandtrade.doc.util.JsonUtil;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeJson.State;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;


public class RestTradeMaker extends OutputMaker {
	
	//TODO remove
	public static final String BASE_URL = "/rest/v1/trades";
	public static final String TRADES_POST_SNIPPET = "TRADES_POST_SNIPPET";
	private static final String TRADES_PUT_SNIPPET = "TRADES_PUT_SNIPPET";	
	private static final String TRADES_GET_SNIPPET = "TRADES_GET_SNIPPET";
	private static final String TRADES_DELETE_SNIPPET = "TRADES_DELETE_SNIPPET";
	private static final String TRADES_SEARCH_SNIPPET = "TRADES_SEARCH_SNIPPET";
	private static final String TRADES_GET_ALL_SNIPPET = "TRADES_GET_ALL_SNIPPET";
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		SnippetFactory snippetFactory = new SnippetFactory(MatchAndTradeRestUtil.getLastAuthorizationHeader());
		
		// TRADES_POST_SNIPPET
		TradeJson tradeJson = new TradeJson();
		tradeJson.setName("Board games");
		Snippet postSnippet = snippetFactory.makeSnippet(Method.POST, ContentType.JSON, tradeJson, MatchAndTradeRestUtil.tradesUrl() + "/");
		postSnippet.getResponse().then().statusCode(201).and().body("", hasKey("tradeId"));
		template = TemplateUtil.replacePlaceholder(template, TRADES_POST_SNIPPET, postSnippet.asHtml());
		tradeJson = JsonUtil.fromResponse(postSnippet.getResponse(), TradeJson.class);
		
		// TRADES_PUT_SNIPPET
		Integer tradeId = tradeJson.getTradeId();
		tradeJson.setTradeId(null); // Set as null because we do not want the id to be displayed on the request body to emphasize that the id must be sent on the URL 
		tradeJson.setName("Board games in Toronto");
		tradeJson.setState(State.MATCHING_ITEMS);
		Snippet putSnippet = snippetFactory.makeSnippet(Method.PUT, ContentType.JSON, tradeJson, MatchAndTradeRestUtil.tradesUrl(tradeId));
		putSnippet.getResponse().then().statusCode(200).and().body("name", equalTo(tradeJson.getName()));
		template = TemplateUtil.replacePlaceholder(template, TRADES_PUT_SNIPPET, putSnippet.asHtml());
		
		// TRADES_GET_SNIPPET
		Snippet getSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.tradesUrl(tradeId));
		getSnippet.getRequest().then().statusCode(200).and().body("tradeId", equalTo(tradeId));
		template = TemplateUtil.replacePlaceholder(template, TRADES_GET_SNIPPET, getSnippet.asHtml());
		
		// TRADES_SEARCH_SNIPPET
		RequestSpecification searchRequest = new RequestSpecBuilder()
				.addHeaders(MatchAndTradeRestUtil.getLastAuthorizationHeaderAsMap())
				.addParam("name", "Board games in Toronto")
				.addParam("_pageNumber", "1")
				.addParam("_pageSize", "1")
				.build();
		Snippet searchSnippet = SnippetFactory.makeSnippet(Method.GET, searchRequest, MatchAndTradeRestUtil.tradesUrl()); 
		searchSnippet.getResponse().then().statusCode(200).and().headers("X-Pagination-Total-Count", equalTo("1"));
		template = TemplateUtil.replacePlaceholder(template, TRADES_SEARCH_SNIPPET, searchSnippet.asHtml());

		// TRADES_GET_ALL_SNIPPET
		Snippet getAllSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.tradesUrl());
		getAllSnippet.getResponse().then().statusCode(200).and().headers("X-Pagination-Total-Count", notNullValue());
		template = TemplateUtil.replacePlaceholder(template, TRADES_GET_ALL_SNIPPET, getSnippet.asHtml());
		
		// TRADES_DELETE_SNIPPET
		Snippet deleteSnippet = snippetFactory.makeSnippet(Method.DELETE, MatchAndTradeRestUtil.tradesUrl(tradeId));
		deleteSnippet.getResponse().then().statusCode(204);
		template = TemplateUtil.replacePlaceholder(template, TRADES_DELETE_SNIPPET, deleteSnippet.asHtml());

		return template;
	}

	@Override
	public String getDocLocation() {
		return "trades.html";
	}

}
