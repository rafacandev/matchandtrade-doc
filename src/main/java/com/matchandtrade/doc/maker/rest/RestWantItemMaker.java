package com.matchandtrade.doc.maker.rest;

import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.github.rafasantos.restdocmaker.util.JsonUtil;
import com.matchandtrade.doc.maker.OutputMaker;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.rest.v1.json.ItemJson;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;
import com.matchandtrade.rest.v1.json.WantItemJson;

import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;
import io.restassured.http.Method;


public class RestWantItemMaker extends OutputMaker {
	
	public static final String BASE_URL = "/want-items";
	public static final String WANT_ITEMS_POST_SNIPPET = "WANT_ITEMS_POST_SNIPPET";
	public static final String WANT_ITEMS_GET_SNIPPET = "WANT_ITEMS_GET_SNIPPET";
	public static final String WANT_ITEMS_GET_ALL_SNIPPET = "WANT_ITEMS_GET_ALL_SNIPPET";
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		
		// Setup owner item
		MatchAndTradeApiFacade matchAndTradeApiFacadeOwner = new MatchAndTradeApiFacade();
		TradeJson tradeOwner = matchAndTradeApiFacadeOwner.createTrade("Owner");
		TradeMembershipJson tradeMembershipOwner = matchAndTradeApiFacadeOwner.findTradeMembershipByUserIdAndTradeId(MatchAndTradeRestUtil.getLastAuthenticatedUserId(), tradeOwner.getTradeId());
		ItemJson alphaJson = matchAndTradeApiFacadeOwner.createItem(tradeMembershipOwner, "Alpha");
		SnippetFactory snippetFactoryOwner = new SnippetFactory(MatchAndTradeRestUtil.getLastAuthorizationHeader());
		
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
				ContentType.JSON,
				wantsBetaPriorityOne,
				MatchAndTradeRestUtil.wantItemsUrl(tradeMembershipOwner.getTradeMembershipId(), alphaJson.getItemId()));
		postSnippet.getResponse().then().statusCode(201).and().body("wantItemId", notNullValue());
		wantsBetaPriorityOne = JsonUtil.fromHttpResponse(postSnippet.getResponse(), WantItemJson.class);
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
		
		return template;
	}

	@Override
	public String getDocLocation() {
		return "want-items.html";
	}

}
