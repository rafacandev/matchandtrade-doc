package com.matchandtrade.doc.maker.rest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.github.rafasantos.restdocmaker.util.JsonUtil;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.rest.v1.json.ItemJson;
import com.matchandtrade.rest.v1.json.TradeJson;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;


public class ItemRestDocMaker implements RestDocMaker {
	
	private static final String ITEMS_POST_PLACEHOLDER = "ITEMS_POST_PLACEHOLDER";
	private static final String ITEMS_PUT_PLACEHOLDER = "ITEMS_PUT_PLACEHOLDER";
	private static final String ITEMS_GET_PLACEHOLDER = "ITEMS_GET_PLACEHOLDER";
	private static final String ITEMS_SEARCH_PLACEHOLDER = "ITEMS_SEARCH_PLACEHOLDER";
	private static final String ITEMS_GET_ALL_PLACEHOLDER = "ITEMS_GET_ALL_PLACEHOLDER";

	@Override
	public String contentFilePath() {
		return "items.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());
		SnippetFactory snippetFactory = new SnippetFactory(ContentType.JSON, MatchAndTradeRestUtil.getLastAuthorizationHeader());
		MatchAndTradeApiFacade matchAndTradeApiFacade = new MatchAndTradeApiFacade();
		
		// Create a trade membership
		TradeJson tradeJson = matchAndTradeApiFacade.createTrade("Board games in Montreal");
		Integer tradeMembershipId = matchAndTradeApiFacade.findTradeMembershipByUserIdAndTradeId(MatchAndTradeRestUtil.getLastAuthenticatedUserId(), tradeJson.getTradeId()).getTradeMembershipId();
		
		// ITEMS_POST_PLACEHOLDER
		ItemJson itemJson = new ItemJson();
		itemJson.setName("Pandemic Legacy: Season 1");
		Snippet postSnippet = snippetFactory.makeSnippet(Method.POST, itemJson, MatchAndTradeRestUtil.itemsUrl(tradeMembershipId) + "/");
		postSnippet.getResponse().then().statusCode(201).and().body("name", equalTo(itemJson.getName()));
		template = TemplateUtil.replacePlaceholder(template, ITEMS_POST_PLACEHOLDER, postSnippet.asHtml());
		itemJson = JsonUtil.fromResponse(postSnippet.getResponse(), ItemJson.class);
		
		// ITEMS_PUT_PLACEHOLDER
		Integer itemId = itemJson.getItemId();
		itemJson.setItemId(null); // Set as null because we do not want to display in the documentation
		itemJson.setLinks(null); // Set as null because we do not want to display in the documentation
		itemJson.setName(itemJson.getName() + " After PUT");
		Snippet putSnippet = snippetFactory.makeSnippet(Method.PUT, itemJson, MatchAndTradeRestUtil.itemsUrl(tradeMembershipId, itemId));
		putSnippet.getResponse().then().statusCode(200).and().body("name", equalTo(itemJson.getName()));
		template = TemplateUtil.replacePlaceholder(template, ITEMS_PUT_PLACEHOLDER, putSnippet.asHtml());

		// ITEMS_GET_PLACEHOLDER
		Snippet getSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.itemsUrl(tradeMembershipId, itemId));
		getSnippet.getResponse().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, ITEMS_GET_PLACEHOLDER, getSnippet.asHtml());
		
		// ITEMS_GET_ALL_PLACEHOLDER
		Snippet getAllSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.itemsUrl(tradeMembershipId) + "/");
		getAllSnippet.getResponse().then().statusCode(200).and().header("X-Pagination-Total-Count", notNullValue());
		template = TemplateUtil.replacePlaceholder(template, ITEMS_GET_ALL_PLACEHOLDER, getAllSnippet.asHtml());

		// ITEMS_SEARCH_PLACEHOLDER
		RequestSpecification searchRequest = new RequestSpecBuilder()
				.addHeaders(MatchAndTradeRestUtil.getLastAuthorizationHeaderAsMap())
				.addParam("name", itemJson.getName())
				.build();
		Snippet searchSnippet = SnippetFactory.makeSnippet(Method.GET, searchRequest, MatchAndTradeRestUtil.itemsUrl(tradeMembershipId));
		searchSnippet.getResponse().then().statusCode(200).and().header("X-Pagination-Total-Count", "1");
		template = TemplateUtil.replacePlaceholder(template, ITEMS_SEARCH_PLACEHOLDER, searchSnippet.asHtml());
		
		return TemplateUtil.appendHeaderAndFooter(template);
	}

}