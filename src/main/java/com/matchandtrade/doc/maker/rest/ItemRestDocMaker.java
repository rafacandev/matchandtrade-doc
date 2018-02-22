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
import com.matchandtrade.rest.v1.json.ItemJson;
import com.matchandtrade.rest.v1.json.TradeJson;

import io.restassured.http.ContentType;
import io.restassured.http.Method;


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
		MatchAndTradeApiFacade apiFacade = new MatchAndTradeApiFacade();
		SnippetFactory snippetFactory = new SnippetFactory(ContentType.JSON, MatchAndTradeRestUtil.getLastAuthorizationHeader());
		
		// Create a trade membership
		TradeJson trade = apiFacade.createTrade("Board games in Montreal - " + new Date().getTime());
		Integer membershipId = apiFacade.findTradeMembershipByUserIdAndTradeId(MatchAndTradeRestUtil.getLastAuthenticatedUserId(), trade.getTradeId()).getTradeMembershipId();
		
		// ITEMS_POST_PLACEHOLDER
		ItemJson item = new ItemJson();
		item.setName("Pandemic Legacy: Season 1");
		Snippet postSnippet = snippetFactory.makeSnippet(Method.POST, item, MatchAndTradeRestUtil.itemsUrl(membershipId) + "/");
		postSnippet.getResponse().then().statusCode(201).and().body("name", equalTo(item.getName()));
		template = TemplateUtil.replacePlaceholder(template, ITEMS_POST_PLACEHOLDER, postSnippet.asHtml());
		item = JsonUtil.fromResponse(postSnippet.getResponse(), ItemJson.class);
		
		// ITEMS_PUT_PLACEHOLDER
		Integer itemId = item.getItemId();
		item.setItemId(null); // Set as null because we do not want to display in the documentation
		item.setLinks(null); // Set as null because we do not want to display in the documentation
		item.setName(item.getName() + " After PUT");
		Snippet putSnippet = snippetFactory.makeSnippet(Method.PUT, item, MatchAndTradeRestUtil.itemsUrl(membershipId, itemId));
		putSnippet.getResponse().then().statusCode(200).and().body("name", equalTo(item.getName()));
		template = TemplateUtil.replacePlaceholder(template, ITEMS_PUT_PLACEHOLDER, putSnippet.asHtml());

		// ITEMS_GET_PLACEHOLDER
		Snippet getSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.itemsUrl(membershipId, itemId));
		getSnippet.getResponse().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, ITEMS_GET_PLACEHOLDER, getSnippet.asHtml());
		
		// ITEMS_GET_ALL_PLACEHOLDER
		Snippet getAllSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.itemsUrl(membershipId) + "/");
		getAllSnippet.getResponse().then().statusCode(200).and().header("X-Pagination-Total-Count", notNullValue());
		template = TemplateUtil.replacePlaceholder(template, ITEMS_GET_ALL_PLACEHOLDER, getAllSnippet.asHtml());

		// ITEMS_SEARCH_PLACEHOLDER
		Snippet searchSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.itemsUrl(membershipId));
		searchSnippet.getResponse().then().statusCode(200).and().header("X-Pagination-Total-Count", "1");
		template = TemplateUtil.replacePlaceholder(template, ITEMS_SEARCH_PLACEHOLDER, searchSnippet.asHtml());
		
		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

}
