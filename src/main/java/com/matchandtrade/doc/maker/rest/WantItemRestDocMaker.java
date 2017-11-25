package com.matchandtrade.doc.maker.rest;

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
import com.matchandtrade.rest.v1.json.TradeMembershipJson;
import com.matchandtrade.rest.v1.json.WantItemJson;

import io.restassured.http.ContentType;
import io.restassured.http.Method;


public class WantItemRestDocMaker implements RestDocMaker {
	
	private static final String WANT_ITEMS_POST_SNIPPET = "WANT_ITEMS_POST_SNIPPET";
	private static final String WANT_ITEMS_GET_SNIPPET = "WANT_ITEMS_GET_SNIPPET";
	private static final String WANT_ITEMS_GET_ALL_SNIPPET = "WANT_ITEMS_GET_ALL_SNIPPET";


	@Override
	public String contentFilePath() {
		return "want-items.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());

		// Setup owner item
		MatchAndTradeApiFacade matchAndTradeApiFacadeOwner = new MatchAndTradeApiFacade();
		TradeJson tradeOwner = matchAndTradeApiFacadeOwner.createTrade("Owner");
		TradeMembershipJson tradeMembershipOwner = matchAndTradeApiFacadeOwner.findTradeMembershipByUserIdAndTradeId(MatchAndTradeRestUtil.getLastAuthenticatedUserId(), tradeOwner.getTradeId());
		ItemJson alphaJson = matchAndTradeApiFacadeOwner.createItem(tradeMembershipOwner, "Alpha");
		SnippetFactory snippetFactoryOwner = new SnippetFactory(ContentType.JSON, MatchAndTradeRestUtil.getLastAuthorizationHeader());
		
		// Setup member item
		MatchAndTradeApiFacade matchAndTradeApiFacadeMember = new MatchAndTradeApiFacade(MatchAndTradeRestUtil.nextAuthorizationHeader());
		TradeMembershipJson tradeMembershipMember = matchAndTradeApiFacadeMember.subscribeToTrade(MatchAndTradeRestUtil.getLastAuthenticatedUserId(), tradeOwner.getTradeId());
		ItemJson argentinaJson = matchAndTradeApiFacadeMember.createItem(tradeMembershipMember, "Argentina");
		
		// WANT_ITEMS_POST_SNIPPET: alpha wants beta
		WantItemJson wantsBetaPriorityOne = new WantItemJson();
		wantsBetaPriorityOne.setItemId(argentinaJson.getItemId());
		wantsBetaPriorityOne.setPriority(1);
		Snippet postSnippet = snippetFactoryOwner.makeSnippet(
				Method.POST,
				wantsBetaPriorityOne,
				MatchAndTradeRestUtil.wantItemsUrl(tradeMembershipOwner.getTradeMembershipId(), alphaJson.getItemId()));
		postSnippet.getResponse().then().statusCode(201).and().body("wantItemId", notNullValue());
		wantsBetaPriorityOne = JsonUtil.fromResponse(postSnippet.getResponse(), WantItemJson.class);
		template = TemplateUtil.replacePlaceholder(template, WANT_ITEMS_POST_SNIPPET, postSnippet.asHtml());
		
		// WANT_ITEMS_GET_SNIPPET
		Snippet getSnippet = snippetFactoryOwner.makeSnippet(
					MatchAndTradeRestUtil.wantItemsUrl(tradeMembershipOwner.getTradeMembershipId(), alphaJson.getItemId(), wantsBetaPriorityOne.getWantItemId())
				);
		getSnippet.getResponse().then().statusCode(200).and().body("wantItemId", notNullValue());
		template = TemplateUtil.replacePlaceholder(template, WANT_ITEMS_GET_SNIPPET, getSnippet.asHtml());
		
		// WANT_ITEMS_GET_ALL_SNIPPET
		Snippet getAllSnippet = snippetFactoryOwner.makeSnippet(MatchAndTradeRestUtil.wantItemsUrl(tradeMembershipOwner.getTradeMembershipId(), alphaJson.getItemId()));
		getAllSnippet.getResponse().then().statusCode(200).and().header("X-Pagination-Total-Count", notNullValue());
		template = TemplateUtil.replacePlaceholder(template, WANT_ITEMS_GET_ALL_SNIPPET, getAllSnippet.asHtml());
		
		return TemplateUtil.appendHeaderAndFooter(template);
	}

}
