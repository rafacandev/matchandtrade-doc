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
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;


public class TradeMembershipRestDocMaker implements RestDocMaker {
	
	private static final String TRADES_MEMBERSHIP_POST_SNIPPET = "TRADES_MEMBERSHIP_POST_SNIPPET";
	private static final String TRADES_MEMBERSHIP_GET_SNIPPET = "TRADES_MEMBERSHIP_GET_SNIPPET";
	private static final String TRADES_MEMBERSHIP_GET_ALL_SNIPPET = "TRADES_MEMBERSHIP_GET_ALL_SNIPPET";
	private static final String TRADES_MEMBERSHIP_SEARCH_SNIPPET = "TRADES_MEMBERSHIP_SEARCH_SNIPPET";
	private static final String TRADES_MEMBERSHIP_DELETE_SNIPPET = "TRADES_MEMBERSHIP_DELETE_SNIPPET";

	@Override
	public String contentFilePath() {
		return "trade-memberships.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());
		MatchAndTradeApiFacade matchAndTradeApiFacadePreviousAuthenticatedUser = new MatchAndTradeApiFacade();
		SnippetFactory snippetFactory = new SnippetFactory(ContentType.JSON, MatchAndTradeRestUtil.nextAuthorizationHeader());

		// TRADES_MEMBERSHIP_POST_SNIPPET
		TradeJson tradeJson = matchAndTradeApiFacadePreviousAuthenticatedUser.createTrade("Board games in Vancouver");
		TradeMembershipJson tradeMembershipJson = new TradeMembershipJson();
		tradeMembershipJson.setTradeId(tradeJson.getTradeId());
		tradeMembershipJson.setUserId(MatchAndTradeRestUtil.getLastAuthenticatedUserId());
		
		Snippet postSnippet = snippetFactory.makeSnippet(Method.POST, tradeMembershipJson, MatchAndTradeRestUtil.tradeMembershipsUrl() + "/");
		postSnippet.getResponse().then().statusCode(201).and().body("tradeMembershipId", notNullValue());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_POST_SNIPPET, postSnippet.asHtml());
		tradeMembershipJson = JsonUtil.fromResponse(postSnippet.getResponse(), TradeMembershipJson.class);

		// TRADES_MEMBERSHIP_GET_SNIPPET
		Snippet getSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.tradeMembershipsUrl(tradeMembershipJson.getTradeMembershipId()));
		getSnippet.getResponse().then().statusCode(200).and().body("tradeMembershipId", equalTo(tradeMembershipJson.getTradeMembershipId()));
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_GET_SNIPPET, getSnippet.asHtml());

		// TRADES_MEMBERSHIP_GET_ALL_SNIPPET
		Snippet getAllSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.tradeMembershipsUrl());
		getAllSnippet.getResponse().then().statusCode(200).and().header("X-Pagination-Total-Count", notNullValue());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_GET_ALL_SNIPPET, getAllSnippet.asHtml());
		
		// TRADES_MEMBERSHIP_SEARCH_SNIPPET
		RequestSpecification searchRequest = new RequestSpecBuilder()
				.addHeaders(MatchAndTradeRestUtil.getLastAuthorizationHeaderAsMap())
				.addParam("userId", MatchAndTradeRestUtil.getLastAuthenticatedUserId())
				.addParam("_pageNumber", "1")
				.addParam("_pageSize", "1")
				.build();
		Snippet searchSnippet = SnippetFactory.makeSnippet(Method.GET, searchRequest, MatchAndTradeRestUtil.tradeMembershipsUrl()); 
		searchSnippet.getResponse().then().statusCode(200).and().headers("X-Pagination-Total-Count", notNullValue());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_SEARCH_SNIPPET, searchSnippet.asHtml());

		// TRADES_MEMBERSHIP_DELETE_SNIPPET
		Snippet deleteSnippet = snippetFactory.makeSnippet(Method.DELETE, MatchAndTradeRestUtil.tradeMembershipsUrl(tradeMembershipJson.getTradeMembershipId()));
		deleteSnippet.getResponse().then().statusCode(204);
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_DELETE_SNIPPET, deleteSnippet.asHtml());

		return TemplateUtil.appendHeaderAndFooter(template);
	}

}
