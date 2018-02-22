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
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;


public class TradeMembershipRestDocMaker implements RestDocMaker {
	
	private static final String TRADES_MEMBERSHIP_POST_PLACEHOLDER = "TRADES_MEMBERSHIP_POST_PLACEHOLDER";
	private static final String TRADES_MEMBERSHIP_GET_PLACEHOLDER = "TRADES_MEMBERSHIP_GET_PLACEHOLDER";
	private static final String TRADES_MEMBERSHIP_GET_ALL_PLACEHOLDER = "TRADES_MEMBERSHIP_GET_ALL_PLACEHOLDER";
	private static final String TRADES_MEMBERSHIP_SEARCH_PLACEHOLDER = "TRADES_MEMBERSHIP_SEARCH_PLACEHOLDER";
	private static final String TRADES_MEMBERSHIP_DELETE_PLACEHOLDER = "TRADES_MEMBERSHIP_DELETE_PLACEHOLDER";

	@Override
	public String contentFilePath() {
		return "trade-memberships.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());
		MatchAndTradeApiFacade matchAndTradeApiFacadePreviousAuthenticatedUser = new MatchAndTradeApiFacade();
		SnippetFactory snippetFactory = new SnippetFactory(ContentType.JSON, MatchAndTradeRestUtil.nextAuthorizationHeader());

		// TRADES_MEMBERSHIP_POST_PLACEHOLDER
		TradeJson tradeJson = matchAndTradeApiFacadePreviousAuthenticatedUser.createTrade("Board games in Vancouver - " + new Date());
		TradeMembershipJson tradeMembershipJson = new TradeMembershipJson();
		tradeMembershipJson.setTradeId(tradeJson.getTradeId());
		tradeMembershipJson.setUserId(MatchAndTradeRestUtil.getLastAuthenticatedUserId());
		
		Snippet postSnippet = snippetFactory.makeSnippet(Method.POST, tradeMembershipJson, MatchAndTradeRestUtil.tradeMembershipsUrl() + "/");
		postSnippet.getResponse().then().statusCode(201).and().body("tradeMembershipId", notNullValue());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_POST_PLACEHOLDER, postSnippet.asHtml());
		tradeMembershipJson = JsonUtil.fromResponse(postSnippet.getResponse(), TradeMembershipJson.class);

		// TRADES_MEMBERSHIP_GET_PLACEHOLDER
		Snippet getSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.tradeMembershipsUrl(tradeMembershipJson.getTradeMembershipId()));
		getSnippet.getResponse().then().statusCode(200).and().body("tradeMembershipId", equalTo(tradeMembershipJson.getTradeMembershipId()));
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_GET_PLACEHOLDER, getSnippet.asHtml());

		// TRADES_MEMBERSHIP_GET_ALL_PLACEHOLDER
		Snippet getAllSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.tradeMembershipsUrl());
		getAllSnippet.getResponse().then().statusCode(200).and().header("X-Pagination-Total-Count", notNullValue());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_GET_ALL_PLACEHOLDER, getAllSnippet.asHtml());
		
		// TRADES_MEMBERSHIP_SEARCH_PLACEHOLDER
		RequestSpecification searchRequest = new RequestSpecBuilder()
				.addHeaders(MatchAndTradeRestUtil.getLastAuthorizationHeaderAsMap())
				.addParam("userId", MatchAndTradeRestUtil.getLastAuthenticatedUserId())
				.addParam("_pageNumber", "1")
				.addParam("_pageSize", "1")
				.build();
		Snippet searchSnippet = SnippetFactory.makeSnippet(Method.GET, searchRequest, MatchAndTradeRestUtil.tradeMembershipsUrl()); 
		searchSnippet.getResponse().then().statusCode(200).and().headers("X-Pagination-Total-Count", notNullValue());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_SEARCH_PLACEHOLDER, searchSnippet.asHtml());

		// TRADES_MEMBERSHIP_DELETE_PLACEHOLDER
		Snippet deleteSnippet = snippetFactory.makeSnippet(Method.DELETE, MatchAndTradeRestUtil.tradeMembershipsUrl(tradeMembershipJson.getTradeMembershipId()));
		deleteSnippet.getResponse().then().statusCode(204);
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_DELETE_PLACEHOLDER, deleteSnippet.asHtml());

		template = PaginationTemplateUtil.replacePaginationRows(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

}
