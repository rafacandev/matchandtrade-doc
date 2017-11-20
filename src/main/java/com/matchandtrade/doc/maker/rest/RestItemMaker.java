package com.matchandtrade.doc.maker.rest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.github.rafasantos.restdocmaker.util.JsonUtil;
import com.matchandtrade.doc.maker.OutputMaker;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.rest.v1.json.ItemJson;
import com.matchandtrade.rest.v1.json.TradeJson;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;


public class RestItemMaker extends OutputMaker {
	
	// TODO remove
	public static final String BASE_URL = "/items";
	public static final String ITEMS_POST_SNIPPET = "ITEMS_POST_SNIPPET";
	public static final String ITEMS_PUT_SNIPPET = "ITEMS_PUT_SNIPPET";
	public static final String ITEMS_GET_SNIPPET = "ITEMS_GET_SNIPPET";
	public static final String ITEMS_SEARCH_SNIPPET = "ITEMS_SEARCH_SNIPPET";
	public static final String ITEMS_GET_ALL_SNIPPET = "ITEMS_GET_ALL_SNIPPET";
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		SnippetFactory snippetFactory = new SnippetFactory(MatchAndTradeRestUtil.getLastAuthorizationHeader());
		MatchAndTradeApiFacade matchAndTradeApiFacade = new MatchAndTradeApiFacade();
		
		// Create a trade membership
		TradeJson tradeJson = matchAndTradeApiFacade.createTrade("Board games in Montreal");
		Integer tradeMembershipId = matchAndTradeApiFacade.findTradeMembershipByUserIdAndTradeId(MatchAndTradeRestUtil.getLastAuthenticatedUserId(), tradeJson.getTradeId()).getTradeMembershipId();
		
		// ITEMS_POST_SNIPPET
		ItemJson itemJson = new ItemJson();
		itemJson.setName("Pandemic Legacy: Season 1");
		Snippet postSnippet = snippetFactory.makeSnippet(Method.POST, ContentType.JSON, itemJson, MatchAndTradeRestUtil.itemsUrl(tradeMembershipId) + "/");
		postSnippet.getResponse().then().statusCode(201).and().body("name", equalTo(itemJson.getName()));
		template = TemplateUtil.replacePlaceholder(template, ITEMS_POST_SNIPPET, postSnippet.asHtml());
		itemJson = JsonUtil.fromHttpResponse(postSnippet.getResponse(), ItemJson.class);
		
		// ITEMS_PUT_SNIPPET
		Integer itemId = itemJson.getItemId();
		itemJson.setItemId(null); // Set as null because we do not want the id to be displayed on the request body to emphasize that the id must be sent on the URL
		itemJson.setName(itemJson.getName() + " After PUT");
		Snippet putSnippet = snippetFactory.makeSnippet(Method.PUT, ContentType.JSON, itemJson, MatchAndTradeRestUtil.itemsUrl(tradeMembershipId, itemId));
		putSnippet.getResponse().then().statusCode(200).and().body("name", equalTo(itemJson.getName()));
		template = TemplateUtil.replacePlaceholder(template, ITEMS_PUT_SNIPPET, putSnippet.asHtml());

		// ITEMS_GET_SNIPPET
		Snippet getSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.itemsUrl(tradeMembershipId, itemId));
		getSnippet.getResponse().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, ITEMS_GET_SNIPPET, getSnippet.asHtml());
		
		// ITEMS_GET_ALL_SNIPPET
		Snippet getAllSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.itemsUrl(tradeMembershipId) + "/");
		getAllSnippet.getResponse().then().statusCode(200).and().header("X-Pagination-Total-Count", notNullValue());
		template = TemplateUtil.replacePlaceholder(template, ITEMS_GET_ALL_SNIPPET, getAllSnippet.asHtml());

		// ITEMS_SEARCH_SNIPPET
		RequestSpecification searchRequest = new RequestSpecBuilder()
				.addHeaders(MatchAndTradeRestUtil.getLastAuthorizationHeaderAsMap())
				.addParam("name", itemJson.getName())
				.build();
		Snippet searchSnippet = SnippetFactory.makeSnippet(Method.GET, searchRequest, MatchAndTradeRestUtil.itemsUrl(tradeMembershipId));
		searchSnippet.getResponse().then().statusCode(200).and().header("X-Pagination-Total-Count", "1");
		template = TemplateUtil.replacePlaceholder(template, ITEMS_SEARCH_SNIPPET, searchSnippet.asHtml());
		
		return template;
	}

	@Override
	public String getDocLocation() {
		return "items.html";
	}

}
