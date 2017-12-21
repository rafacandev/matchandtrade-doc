package com.matchandtrade.doc.maker;

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

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;

public class UseCaseRestDocMaker implements RestDocMaker {
	
	private static final String MEMBER_AUTHENTICATE_PLACEHOLDER = "MEMBER_AUTHENTICATE_PLACEHOLDER";
	private static final String MEMBER_AUTHENTICATIONS_PLACEHOLDER = "MEMBER_AUTHENTICATIONS_PLACEHOLDER";
	private static final String MEMBER_TRADES_MEMBERSHIP_PLACEHOLDER = "MEMBER_TRADES_MEMBERSHIP_PLACEHOLDER";
	private static final String MEMBER_ITEM_ONE_PLACEHOLDER = "MEMBER_ITEM_ONE_PLACEHOLDER";
	private static final String MEMBER_ITEM_TWO_PLACEHOLDER = "MEMBER_ITEM_TWO_PLACEHOLDER";
	private static final String MEMBER_ITEM_THREE_PLACEHOLDER = "MEMBER_ITEM_THREE_PLACEHOLDER";
	private static final String MEMBER_WANT_ITEMS_ONE = "MEMBER_WANT_ITEMS_ONE";
	private static final String MEMBER_WANT_ITEMS_TWO = "MEMBER_WANT_ITEMS_TWO";
	
	private static final String OWNER_AUTHENTICATE_PLACEHOLDER = "OWNER_AUTHENTICATE_PLACEHOLDER";
	private static final String OWNER_AUTHENTICATIONS_PLACEHOLDER = "OWNER_AUTHENTICATIONS_PLACEHOLDER";
	private static final String OWNER_ITEM_ONE_PLACEHOLDER = "OWNER_ITEM_ONE_PLACEHOLDER";
	private static final String OWNER_ITEM_TWO_PLACEHOLDER = "OWNER_ITEM_TWO_PLACEHOLDER";
	private static final String OWNER_TRADES_POST_PLACEHOLDER = "OWNER_TRADES_POST_PLACEHOLDER";
	private static final String OWNER_TRADE_MEMBERSHIP_PLACEHOLDER = "OWNER_TRADE_MEMBERSHIP_PLACEHOLDER"; 
	private static final String OWNER_WANT_ITEMS_ONE = "OWNER_WANT_ITEMS_ONE";
	
	private static final String TRADE_MATCHING_ITEMS_PLACEHOLDER = "TRADE_MATCHING_ITEMS_PLACEHOLDER"; 
	private static final String TRADE_MATCHING_ITEMS_ENDED = "TRADE_MATCHING_ITEMS_ENDED"; 
	private static final String TRADE_RESULTS = "TRADE_RESULTS";

	@Override
	public String contentFilePath() {
		return "use-cases.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());
		
		SnippetFactory snippetFactoryOlavo = new SnippetFactory(ContentType.JSON, MatchAndTradeRestUtil.getLastAuthorizationHeader());
		MatchAndTradeApiFacade matchAndTradeApiFacadeOlavo = new MatchAndTradeApiFacade(MatchAndTradeRestUtil.getLastAuthorizationHeader());
		Integer userIdOlavo = MatchAndTradeRestUtil.getLastAuthenticatedUserId();

		// OWNER_AUTHENTICATE_PLACEHOLDER
		Snippet authenticateSnippetOlavo = snippetFactoryOlavo.makeSnippet(MatchAndTradeRestUtil.authenticateUrl());
		authenticateSnippetOlavo.getRequest().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, OWNER_AUTHENTICATE_PLACEHOLDER, authenticateSnippetOlavo.asHtml());

		// OWNER_AUTHENTICATIONS_PLACEHOLDER
		Snippet authenticationsSnippetOlavo = snippetFactoryOlavo.makeSnippet(MatchAndTradeRestUtil.authenticationsUrl() + "/");
		authenticationsSnippetOlavo.getRequest().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, OWNER_AUTHENTICATIONS_PLACEHOLDER, authenticationsSnippetOlavo.asHtml());
		
		// OWNER_TRADES_POST_PLACEHOLDER
		TradeJson tradeJson = new TradeJson();
		tradeJson.setName("Board games in Ottawa");
		Snippet tradePostOwner = snippetFactoryOlavo.makeSnippet(Method.POST, tradeJson, MatchAndTradeRestUtil.tradesUrl() + "/");
		tradePostOwner.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, OWNER_TRADES_POST_PLACEHOLDER, tradePostOwner.asHtml());
		tradeJson = JsonUtil.fromResponse(tradePostOwner.getResponse(), TradeJson.class);
		
		//OWNER_TRADE_MEMBERSHIP_PLACEHOLDER
		RequestSpecification searchTradeMembershipOlavo = new RequestSpecBuilder()
				.addHeaders(MatchAndTradeRestUtil.getLastAuthorizationHeaderAsMap())
				.setContentType(ContentType.JSON)
				.addParam("userId", userIdOlavo)
				.addParam("tradeId", tradeJson.getTradeId())
				.build();
		Snippet searchTradeMembershipOlavoSnippet = SnippetFactory.makeSnippet(Method.GET, searchTradeMembershipOlavo, MatchAndTradeRestUtil.tradeMembershipsUrl()); 
		searchTradeMembershipOlavoSnippet.getResponse().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, OWNER_TRADE_MEMBERSHIP_PLACEHOLDER, searchTradeMembershipOlavoSnippet.asHtml());

		// OWNER_ITEM_ONE_PLACEHOLDER
		Integer tradeMembershipIdOlavo = matchAndTradeApiFacadeOlavo.findTradeMembershipByUserIdAndTradeId(userIdOlavo, tradeJson.getTradeId()).getTradeMembershipId();
		ItemJson pandemicOneJson = new ItemJson();
		pandemicOneJson.setName("Pandemic Legacy: Season 1");
		Snippet pandemicOneSnippet = snippetFactoryOlavo.makeSnippet(Method.POST, pandemicOneJson, MatchAndTradeRestUtil.itemsUrl(tradeMembershipIdOlavo) + "/");
		pandemicOneSnippet.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, OWNER_ITEM_ONE_PLACEHOLDER, pandemicOneSnippet.asHtml());
		pandemicOneJson = JsonUtil.fromResponse(pandemicOneSnippet.getResponse(), ItemJson.class);
		
		// OWNER_ITEM_TWO_PLACEHOLDER
		ItemJson pandemicTwoJson = new ItemJson();
		pandemicTwoJson.setName("Pandemic Legacy: Season 2");
		Snippet pandemicTwoSnippet = snippetFactoryOlavo.makeSnippet(Method.POST, pandemicTwoJson, MatchAndTradeRestUtil.itemsUrl(tradeMembershipIdOlavo) + "/");
		pandemicTwoSnippet.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, OWNER_ITEM_TWO_PLACEHOLDER, pandemicTwoSnippet.asHtml());
		pandemicTwoJson = JsonUtil.fromResponse(pandemicTwoSnippet.getResponse(), ItemJson.class);
		
		// MEMBER SETUP
		SnippetFactory snippetFactoryMaria = new SnippetFactory(ContentType.JSON, MatchAndTradeRestUtil.nextAuthorizationHeader());
		MatchAndTradeApiFacade matchAndTradeApiFacadeMaria = new MatchAndTradeApiFacade(MatchAndTradeRestUtil.getLastAuthorizationHeader());
		Integer userIdMaria = MatchAndTradeRestUtil.getLastAuthenticatedUserId();

		// MEMBER_AUTHENTICATE_PLACEHOLDER
		Snippet authenticateSnippetMaria = snippetFactoryMaria.makeSnippet(MatchAndTradeRestUtil.authenticateUrl());
		authenticateSnippetMaria.getRequest().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_AUTHENTICATE_PLACEHOLDER, authenticateSnippetOlavo.asHtml());

		// MEMBER_AUTHENTICATIONS_PLACEHOLDER
		Snippet authenticationsSnippetMaria = snippetFactoryMaria.makeSnippet(MatchAndTradeRestUtil.authenticationsUrl() + "/");
		authenticationsSnippetMaria.getRequest().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_AUTHENTICATIONS_PLACEHOLDER, authenticationsSnippetMaria.asHtml());

		// MEMBER_TRADES_MEMBERSHIP_PLACEHOLDER
		TradeMembershipJson tradeMembershipJsonMaria = new TradeMembershipJson();
		tradeMembershipJsonMaria.setTradeId(tradeJson.getTradeId());
		tradeMembershipJsonMaria.setUserId(userIdMaria);
		Snippet tradeMembershipSnippetMaria = snippetFactoryMaria.makeSnippet(Method.POST, tradeMembershipJsonMaria, MatchAndTradeRestUtil.tradeMembershipsUrl() + "/");
		tradeMembershipSnippetMaria.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_TRADES_MEMBERSHIP_PLACEHOLDER, tradeMembershipSnippetMaria.asHtml());
		
		// MEMBER_ITEM_ONE
		Integer tradeMembershipIdMaria = matchAndTradeApiFacadeMaria.findTradeMembershipByUserIdAndTradeId(userIdMaria, tradeJson.getTradeId()).getTradeMembershipId();
		ItemJson stoneAgeJson = new ItemJson();
		stoneAgeJson.setName("Stone Age");
		Snippet stoneAgeSnippet = snippetFactoryMaria.makeSnippet(Method.POST, stoneAgeJson, MatchAndTradeRestUtil.itemsUrl(tradeMembershipIdMaria) + "/");
		stoneAgeSnippet.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_ITEM_ONE_PLACEHOLDER, stoneAgeSnippet.asHtml());
		stoneAgeJson = JsonUtil.fromResponse(stoneAgeSnippet.getResponse(), ItemJson.class);

		// MEMBER_ITEM_TWO
		ItemJson carcassonneJson = new ItemJson();
		carcassonneJson.setName("Carcassonne");
		Snippet carcassonneSnippet = snippetFactoryMaria.makeSnippet(Method.POST, carcassonneJson, MatchAndTradeRestUtil.itemsUrl(tradeMembershipIdMaria) + "/");
		carcassonneSnippet.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_ITEM_TWO_PLACEHOLDER, carcassonneSnippet.asHtml());

		// MEMBER_ITEM_THREE
		ItemJson noThanksJson = new ItemJson();
		noThanksJson.setName("No Thanks!");
		Snippet noThanksSnippet = snippetFactoryMaria.makeSnippet(Method.POST, noThanksJson, MatchAndTradeRestUtil.itemsUrl(tradeMembershipIdMaria) + "/");
		noThanksSnippet.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_ITEM_THREE_PLACEHOLDER, noThanksSnippet.asHtml());

		// TRADE_MATCHING_ITEMS_PLACEHOLDER
		tradeJson.setState(TradeJson.State.MATCHING_ITEMS);
		Integer tradeId = tradeJson.getTradeId();
		tradeJson.setTradeId(null); // Set as null because we do not want to display in the documentation
		tradeJson.setLinks(null); // Set as null because we do not want to display in the documentation
		Snippet tradePutOwner = snippetFactoryOlavo.makeSnippet(Method.PUT, tradeJson, MatchAndTradeRestUtil.tradesUrl(tradeId));
		tradePutOwner.getResponse().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, TRADE_MATCHING_ITEMS_PLACEHOLDER, tradePutOwner.asHtml());
		
		// OWNER_WANT_ITEMS_ONE
		WantItemJson wantsStoneAge = new WantItemJson();
		wantsStoneAge.setPriority(0);
		wantsStoneAge.setItemId(stoneAgeJson.getItemId());
		Snippet wantsStoneAgeSnippet = snippetFactoryOlavo.makeSnippet(
				Method.POST,
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
				wantsPandemicTwo,
				MatchAndTradeRestUtil.wantItemsUrl(tradeMembershipIdMaria, stoneAgeJson.getItemId()));
		wantsPandemicTwoSnippet.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_WANT_ITEMS_TWO, wantsPandemicTwoSnippet.asHtml());

		// TRADE_MATCHING_ITEMS_ENDED
		tradeJson.setState(TradeJson.State.MATCHING_ITEMS_ENDED);
		Snippet tradePutEndSnippet = snippetFactoryOlavo.makeSnippet(Method.PUT, tradeJson, MatchAndTradeRestUtil.tradesUrl(tradeId));
		tradePutEndSnippet.getResponse().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, TRADE_MATCHING_ITEMS_ENDED, tradePutEndSnippet.asHtml());		
		
		// TRADE_RESULTS
		Snippet tradeResultsSnippet = snippetFactoryOlavo.makeSnippet(MatchAndTradeRestUtil.tradeResultsUrl(tradeId));
		template = TemplateUtil.replacePlaceholder(template, TRADE_RESULTS, tradeResultsSnippet.asHtml());
		
		return TemplateUtil.appendHeaderAndFooter(template);
	}

}
