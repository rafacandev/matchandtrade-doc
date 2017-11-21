package com.matchandtrade.doc.maker;

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

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;

public class RestUseCaseMaker extends OutputMaker {
	
	private static final String MEMBER_AUTHENTICATE_SNIPPET = "MEMBER_AUTHENTICATE_SNIPPET";
	private static final String MEMBER_AUTHENTICATIONS_SNIPPET = "MEMBER_AUTHENTICATIONS_SNIPPET";
	private static final String MEMBER_TRADES_MEMBERSHIP_SNIPPET = "MEMBER_TRADES_MEMBERSHIP_SNIPPET";
	private static final String MEMBER_ITEM_ONE_SNIPPET = "MEMBER_ITEM_ONE_SNIPPET";
	private static final String MEMBER_ITEM_TWO_SNIPPET = "MEMBER_ITEM_TWO_SNIPPET";
	private static final String MEMBER_ITEM_THREE_SNIPPET = "MEMBER_ITEM_THREE_SNIPPET";
	private static final String MEMBER_WANT_ITEMS_ONE = "MEMBER_WANT_ITEMS_ONE";
	private static final String MEMBER_WANT_ITEMS_TWO = "MEMBER_WANT_ITEMS_TWO";
	
	private static final String OWNER_AUTHENTICATE_SNIPPET = "OWNER_AUTHENTICATE_SNIPPET";
	private static final String OWNER_AUTHENTICATIONS_SNIPPET = "OWNER_AUTHENTICATIONS_SNIPPET";
	private static final String OWNER_ITEM_ONE_SNIPPET = "OWNER_ITEM_ONE_SNIPPET";
	private static final String OWNER_ITEM_TWO_SNIPPET = "OWNER_ITEM_TWO_SNIPPET";
	private static final String OWNER_TRADES_POST_SNIPPET = "OWNER_TRADES_POST_SNIPPET";
	private static final String OWNER_TRADE_MEMBERSHIP_SNIPPET = "OWNER_TRADE_MEMBERSHIP_SNIPPET"; 
	private static final String OWNER_WANT_ITEMS_ONE = "OWNER_WANT_ITEMS_ONE";
	
	private static final String TRADE_MATCHING_ITEMS_SNIPPET = "TRADE_MATCHING_ITEMS_SNIPPET"; 
	private static final String TRADE_MATCHING_ITEMS_ENDED = "TRADE_MATCHING_ITEMS_ENDED"; 
	private static final String TRADE_RESULTS = "TRADE_RESULTS";
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		
		SnippetFactory snippetFactoryOlavo = new SnippetFactory(MatchAndTradeRestUtil.getLastAuthorizationHeader());
		MatchAndTradeApiFacade matchAndTradeApiFacadeOlavo = new MatchAndTradeApiFacade(MatchAndTradeRestUtil.getLastAuthorizationHeader());
		Integer userIdOlavo = MatchAndTradeRestUtil.getLastAuthenticatedUserId();

		// OWNER_AUTHENTICATE_SNIPPET
		Snippet authenticateSnippetOlavo = snippetFactoryOlavo.makeSnippet(MatchAndTradeRestUtil.authenticateUrl());
		authenticateSnippetOlavo.getRequest().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, OWNER_AUTHENTICATE_SNIPPET, authenticateSnippetOlavo.asHtml());

		// OWNER_AUTHENTICATIONS_SNIPPET
		Snippet authenticationsSnippetOlavo = snippetFactoryOlavo.makeSnippet(MatchAndTradeRestUtil.authenticationsUrl() + "/");
		authenticationsSnippetOlavo.getRequest().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, OWNER_AUTHENTICATIONS_SNIPPET, authenticationsSnippetOlavo.asHtml());
		
		// OWNER_TRADES_POST_SNIPPET
		TradeJson tradeJson = new TradeJson();
		tradeJson.setName("Board games in Ottawa");
		Snippet tradePostOwner = snippetFactoryOlavo.makeSnippet(Method.POST, ContentType.JSON, tradeJson, MatchAndTradeRestUtil.tradesUrl() + "/");
		tradePostOwner.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, OWNER_TRADES_POST_SNIPPET, tradePostOwner.asHtml());
		tradeJson = JsonUtil.fromHttpResponse(tradePostOwner.getResponse(), TradeJson.class);
		
		//OWNER_TRADE_MEMBERSHIP_SNIPPET
		RequestSpecification searchTradeMembershipOlavo = new RequestSpecBuilder()
				.addHeaders(MatchAndTradeRestUtil.getLastAuthorizationHeaderAsMap())
				.addParam("userId", userIdOlavo)
				.addParam("tradeId", tradeJson.getTradeId())
				.build();
		Snippet searchTradeMembershipOlavoSnippet = SnippetFactory.makeSnippet(Method.GET, searchTradeMembershipOlavo, MatchAndTradeRestUtil.tradeMembershipsUrl()); 
		searchTradeMembershipOlavoSnippet.getResponse().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, OWNER_TRADE_MEMBERSHIP_SNIPPET, searchTradeMembershipOlavoSnippet.asHtml());

		// OWNER_ITEM_ONE_SNIPPET
		Integer tradeMembershipIdOlavo = matchAndTradeApiFacadeOlavo.findTradeMembershipByUserIdAndTradeId(userIdOlavo, tradeJson.getTradeId()).getTradeMembershipId();
		ItemJson pandemicOneJson = new ItemJson();
		pandemicOneJson.setName("Pandemic Legacy: Season 1");
		Snippet pandemicOneSnippet = snippetFactoryOlavo.makeSnippet(Method.POST, ContentType.JSON, pandemicOneJson, MatchAndTradeRestUtil.itemsUrl(tradeMembershipIdOlavo) + "/");
		pandemicOneSnippet.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, OWNER_ITEM_ONE_SNIPPET, pandemicOneSnippet.asHtml());
		pandemicOneJson = JsonUtil.fromHttpResponse(pandemicOneSnippet.getResponse(), ItemJson.class);
		
		// OWNER_ITEM_TWO_SNIPPET
		ItemJson pandemicTwoJson = new ItemJson();
		pandemicTwoJson.setName("Pandemic Legacy: Season 2");
		Snippet pandemicTwoSnippet = snippetFactoryOlavo.makeSnippet(Method.POST, ContentType.JSON, pandemicTwoJson, MatchAndTradeRestUtil.itemsUrl(tradeMembershipIdOlavo) + "/");
		pandemicTwoSnippet.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, OWNER_ITEM_TWO_SNIPPET, pandemicTwoSnippet.asHtml());
		pandemicTwoJson = JsonUtil.fromHttpResponse(pandemicTwoSnippet.getResponse(), ItemJson.class);
		
		// MEMBER SETUP
		SnippetFactory snippetFactoryMaria = new SnippetFactory(MatchAndTradeRestUtil.nextAuthorizationHeader());
		MatchAndTradeApiFacade matchAndTradeApiFacadeMaria = new MatchAndTradeApiFacade(MatchAndTradeRestUtil.getLastAuthorizationHeader());
		Integer userIdMaria = MatchAndTradeRestUtil.getLastAuthenticatedUserId();

		// MEMBER_AUTHENTICATE_SNIPPET
		Snippet authenticateSnippetMaria = snippetFactoryMaria.makeSnippet(MatchAndTradeRestUtil.authenticateUrl());
		authenticateSnippetMaria.getRequest().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_AUTHENTICATE_SNIPPET, authenticateSnippetOlavo.asHtml());

		// MEMBER_AUTHENTICATIONS_SNIPPET
		Snippet authenticationsSnippetMaria = snippetFactoryMaria.makeSnippet(MatchAndTradeRestUtil.authenticationsUrl() + "/");
		authenticationsSnippetMaria.getRequest().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_AUTHENTICATIONS_SNIPPET, authenticationsSnippetMaria.asHtml());

		// MEMBER_TRADES_MEMBERSHIP_SNIPPET
		TradeMembershipJson tradeMembershipJsonMaria = new TradeMembershipJson();
		tradeMembershipJsonMaria.setTradeId(tradeJson.getTradeId());
		tradeMembershipJsonMaria.setUserId(userIdMaria);
		Snippet tradeMembershipSnippetMaria = snippetFactoryMaria.makeSnippet(Method.POST, ContentType.JSON, tradeMembershipJsonMaria, MatchAndTradeRestUtil.tradeMembershipsUrl() + "/");
		tradeMembershipSnippetMaria.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_TRADES_MEMBERSHIP_SNIPPET, tradeMembershipSnippetMaria.asHtml());
		
		// MEMBER_ITEM_ONE
		Integer tradeMembershipIdMaria = matchAndTradeApiFacadeMaria.findTradeMembershipByUserIdAndTradeId(userIdMaria, tradeJson.getTradeId()).getTradeMembershipId();
		ItemJson stoneAgeJson = new ItemJson();
		stoneAgeJson.setName("Stone Age");
		Snippet stoneAgeSnippet = snippetFactoryMaria.makeSnippet(Method.POST, ContentType.JSON, stoneAgeJson, MatchAndTradeRestUtil.itemsUrl(tradeMembershipIdMaria) + "/");
		stoneAgeSnippet.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_ITEM_ONE_SNIPPET, stoneAgeSnippet.asHtml());
		stoneAgeJson = JsonUtil.fromHttpResponse(stoneAgeSnippet.getResponse(), ItemJson.class);

		// MEMBER_ITEM_TWO
		ItemJson carcassonneJson = new ItemJson();
		carcassonneJson.setName("Carcassonne");
		Snippet carcassonneSnippet = snippetFactoryMaria.makeSnippet(Method.POST, ContentType.JSON, carcassonneJson, MatchAndTradeRestUtil.itemsUrl(tradeMembershipIdMaria) + "/");
		carcassonneSnippet.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_ITEM_TWO_SNIPPET, carcassonneSnippet.asHtml());

		// MEMBER_ITEM_THREE
		ItemJson noThanksJson = new ItemJson();
		noThanksJson.setName("No Thanks!");
		Snippet noThanksSnippet = snippetFactoryMaria.makeSnippet(Method.POST, ContentType.JSON, noThanksJson, MatchAndTradeRestUtil.itemsUrl(tradeMembershipIdMaria) + "/");
		noThanksSnippet.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_ITEM_THREE_SNIPPET, noThanksSnippet.asHtml());

		// TRADE_MATCHING_ITEMS_SNIPPET
		tradeJson.setState(TradeJson.State.MATCHING_ITEMS);
		Integer tradeId = tradeJson.getTradeId();
		tradeJson.setTradeId(null); // We do not want tradeId displayed in the documentation
		Snippet tradePutOwner = snippetFactoryOlavo.makeSnippet(Method.PUT, ContentType.JSON, tradeJson, MatchAndTradeRestUtil.tradesUrl(tradeId));
		tradePutOwner.getResponse().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, TRADE_MATCHING_ITEMS_SNIPPET, tradePutOwner.asHtml());
		
		// OWNER_WANT_ITEMS_ONE
		WantItemJson wantsStoneAge = new WantItemJson();
		wantsStoneAge.setPriority(0);
		wantsStoneAge.setItemId(stoneAgeJson.getItemId());
		Snippet wantsStoneAgeSnippet = snippetFactoryOlavo.makeSnippet(
				Method.POST,
				ContentType.JSON,
				wantsStoneAge,
				MatchAndTradeRestUtil.wantItemsUrl(tradeMembershipIdOlavo, pandemicOneJson.getItemId()));
		wantsStoneAgeSnippet.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, OWNER_WANT_ITEMS_ONE, wantsStoneAgeSnippet.asHtml());

		// MEMBER_WANT_ITEMS_ONE
		WantItemJson wantsPandemicOne = new WantItemJson();
		wantsPandemicOne.setPriority(1);
		wantsPandemicOne.setItemId(pandemicOneJson.getItemId());
		Snippet wantsPandemicOneSnippet = snippetFactoryMaria.makeSnippet(
				Method.POST,
				ContentType.JSON,
				wantsPandemicOne,
				MatchAndTradeRestUtil.wantItemsUrl(tradeMembershipIdMaria, stoneAgeJson.getItemId()));
		wantsPandemicOneSnippet.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_WANT_ITEMS_ONE, wantsPandemicOneSnippet.asHtml());

		// MEMBER_WANT_ITEMS_TWO
		WantItemJson wantsPandemicTwo = new WantItemJson();
		wantsPandemicTwo.setPriority(2);
		wantsPandemicTwo.setItemId(pandemicTwoJson.getItemId());
		Snippet wantsPandemicTwoSnippet = snippetFactoryMaria.makeSnippet(
				Method.POST,
				ContentType.JSON,
				wantsPandemicTwo,
				MatchAndTradeRestUtil.wantItemsUrl(tradeMembershipIdMaria, stoneAgeJson.getItemId()));
		wantsPandemicTwoSnippet.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_WANT_ITEMS_TWO, wantsPandemicTwoSnippet.asHtml());

		// TRADE_MATCHING_ITEMS_ENDED
		tradeJson.setState(TradeJson.State.MATCHING_ITEMS_ENDED);
		Snippet tradePutEndSnippet = snippetFactoryOlavo.makeSnippet(Method.PUT, ContentType.JSON, tradeJson, MatchAndTradeRestUtil.tradesUrl(tradeId));
		tradePutEndSnippet.getResponse().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, TRADE_MATCHING_ITEMS_ENDED, tradePutEndSnippet.asHtml());		
		
		// TRADE_RESULTS
		Snippet tradeResultsSnippet = snippetFactoryOlavo.makeSnippet(MatchAndTradeRestUtil.tradeResultsUrl(tradeId));
		template = TemplateUtil.replacePlaceholder(template, TRADE_RESULTS, tradeResultsSnippet.asHtml());
		
		return template;
	}

	@Override
	public String getDocLocation() {
		return "use-cases.html";
	}
}
