
package com.matchandtrade.doc.maker.rest;

import static org.hamcrest.Matchers.equalTo;

import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;
import com.matchandtrade.rest.v1.json.UserJson;
import com.matchandtrade.rest.v1.json.search.Recipe;
import com.matchandtrade.rest.v1.json.search.SearchCriteriaJson;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;


public class SearchRestDocMaker implements RestDocMaker {
	
	private static final String SEARCH_POST_PLACEHOLDER = "SEARCH_POST_PLACEHOLDER";

	@Override
	public String contentFilePath() {
		return "search.html";
	}

	@Override
	public String content() {
		UserJson owner = MatchAndTradeRestUtil.getLastAuthenticatedUser();
		
		MatchAndTradeApiFacade apiAsOwner = new MatchAndTradeApiFacade();
		TradeJson trade = apiAsOwner.createTrade("Search Recipe ITEMS");
		TradeMembershipJson tradeMembership = apiAsOwner.findTradeMembershipByUserIdAndTradeId(owner.getUserId(), trade.getTradeId());
		apiAsOwner.createItem(tradeMembership, "Imperial Settlers");
		apiAsOwner.createItem(tradeMembership, "Dead of Winter: A Crossroads Game");
		
		MatchAndTradeRestUtil.nextAuthorizationHeader();
		UserJson member = MatchAndTradeRestUtil.getLastAuthenticatedUser();
		MatchAndTradeApiFacade apiAsMember = new MatchAndTradeApiFacade(MatchAndTradeRestUtil.getLastAuthorizationHeader());
		TradeMembershipJson memberMembership = apiAsMember.subscribeToTrade(member.getUserId(), trade.getTradeId());
		apiAsMember.createItem(memberMembership, "Elysium");
		apiAsMember.createItem(memberMembership, "The Voyages of Marco Polo");
		apiAsMember.createItem(memberMembership, "Deus");

		// SEARCH_POST_PLACEHOLDER
		SearchCriteriaJson search = new SearchCriteriaJson();
		search.setRecipe(Recipe.ITEMS);
		search.addCriterion("trade.tradeId", trade.getTradeId());
		RequestSpecification searchRequest = new RequestSpecBuilder()
				.addHeaders(MatchAndTradeRestUtil.getLastAuthorizationHeaderAsMap())
				.addQueryParam("_pageNumber", "2")
				.addQueryParam("_pageSize", "2")
				.setContentType(ContentType.JSON)
				.setBody(search)
				.build();

		Snippet postSnippet = SnippetFactory.makeSnippet(Method.POST, searchRequest, MatchAndTradeRestUtil.searchUrl());
		postSnippet.getResponse().then().statusCode(200).and().header("X-Pagination-Total-Count", equalTo("5"));
		
		String template = TemplateUtil.buildTemplate(contentFilePath());
		template = TemplateUtil.replacePlaceholder(template, SEARCH_POST_PLACEHOLDER, postSnippet.asHtml());
		return TemplateUtil.appendHeaderAndFooter(template);
	}

}