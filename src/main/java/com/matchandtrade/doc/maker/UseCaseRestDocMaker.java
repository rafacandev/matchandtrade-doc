package com.matchandtrade.doc.maker;

import java.util.Date;

import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.github.rafasantos.restdocmaker.util.JsonUtil;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.rest.v1.json.ItemJson;
import com.matchandtrade.rest.v1.json.OfferJson;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;
import com.matchandtrade.rest.v1.json.UserJson;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;

public class UseCaseRestDocMaker implements RestDocMaker {
	
	private static final String MEMBER_AUTHENTICATE = "MEMBER_AUTHENTICATE";
	private static final String MEMBER_AUTHENTICATIONS = "MEMBER_AUTHENTICATIONS";
	private static final String MEMBER_TRADES_MEMBERSHIP = "MEMBER_TRADES_MEMBERSHIP";
	private static final String MEMBER_ITEM_ONE = "MEMBER_ITEM_ONE";
	private static final String MEMBER_ITEM_TWO = "MEMBER_ITEM_TWO";
	private static final String MEMBER_ITEM_THREE = "MEMBER_ITEM_THREE";
	private static final String MEMBER_OFFER_ONE = "MEMBER_OFFER_ONE";
	private static final String MEMBER_OFFER_TWO = "MEMBER_OFFER_TWO";
	
	private static final String OWNER_AUTHENTICATE = "OWNER_AUTHENTICATE";
	private static final String OWNER_AUTHENTICATIONS = "OWNER_AUTHENTICATIONS";
	private static final String OWNER_ITEM_ONE = "OWNER_ITEM_ONE";
	private static final String OWNER_ITEM_TWO = "OWNER_ITEM_TWO";
	private static final String OWNER_TRADES_POST = "OWNER_TRADES_POST";
	private static final String OWNER_TRADE_MEMBERSHIP = "OWNER_TRADE_MEMBERSHIP"; 
	private static final String OWNER_OFFER_ONE = "OWNER_OFFER_ONE";
	
	private static final String TRADE_MATCHING_ITEMS = "TRADE_MATCHING_ITEMS"; 
	private static final String TRADE_GENERATE_TRADES = "TRADE_GENERATE_TRADES"; 
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
		
		// Building a user with a given user name for documentation clarity
		UserJson userOlavo = MatchAndTradeRestUtil.getLastAuthenticatedUser();
		userOlavo.setName("Olavo");
		matchAndTradeApiFacadeOlavo.saveUser(userOlavo);

		// OWNER_AUTHENTICATE
		Snippet authenticateSnippetOlavo = snippetFactoryOlavo.makeSnippet(MatchAndTradeRestUtil.authenticateUrl());
		authenticateSnippetOlavo.getRequest().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, OWNER_AUTHENTICATE, authenticateSnippetOlavo.asHtml());

		// OWNER_AUTHENTICATIONS
		Snippet authenticationsSnippetOlavo = snippetFactoryOlavo.makeSnippet(MatchAndTradeRestUtil.authenticationsUrl() + "/");
		authenticationsSnippetOlavo.getRequest().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, OWNER_AUTHENTICATIONS, authenticationsSnippetOlavo.asHtml());
		
		// OWNER_TRADES_POST
		TradeJson tradeJson = new TradeJson();
		String tradeName = "Board games in Ottawa - " + new Date().getTime() + this.hashCode();
		tradeJson.setName(tradeName);
		Snippet tradePostOwner = snippetFactoryOlavo.makeSnippet(Method.POST, tradeJson, MatchAndTradeRestUtil.tradesUrl() + "/");
		tradePostOwner.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, OWNER_TRADES_POST, tradePostOwner.asHtml());
		tradeJson = JsonUtil.fromResponse(tradePostOwner.getResponse(), TradeJson.class);
		
		//OWNER_TRADE_MEMBERSHIP
		RequestSpecification searchTradeMembershipOlavo = new RequestSpecBuilder()
				.addHeaders(MatchAndTradeRestUtil.getLastAuthorizationHeaderAsMap())
				.setContentType(ContentType.JSON)
				.addParam("userId", userOlavo.getUserId())
				.addParam("tradeId", tradeJson.getTradeId())
				.build();
		Snippet searchTradeMembershipOlavoSnippet = SnippetFactory.makeSnippet(Method.GET, searchTradeMembershipOlavo, MatchAndTradeRestUtil.tradeMembershipsUrl()); 
		searchTradeMembershipOlavoSnippet.getResponse().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, OWNER_TRADE_MEMBERSHIP, searchTradeMembershipOlavoSnippet.asHtml());

		// OWNER_ITEM_ONE
		Integer tradeMembershipIdOlavo = matchAndTradeApiFacadeOlavo.findTradeMembershipByUserIdAndTradeId(userOlavo.getUserId(), tradeJson.getTradeId()).getTradeMembershipId();
		ItemJson pandemicOneJson = new ItemJson();
		pandemicOneJson.setName("Pandemic Legacy: Season 1");
		Snippet pandemicOneSnippet = snippetFactoryOlavo.makeSnippet(Method.POST, pandemicOneJson, MatchAndTradeRestUtil.itemsUrl(tradeMembershipIdOlavo) + "/");
		pandemicOneSnippet.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, OWNER_ITEM_ONE, pandemicOneSnippet.asHtml());
		pandemicOneJson = JsonUtil.fromResponse(pandemicOneSnippet.getResponse(), ItemJson.class);
		
		// OWNER_ITEM_TWO
		ItemJson pandemicTwoJson = new ItemJson();
		pandemicTwoJson.setName("Pandemic Legacy: Season 2");
		Snippet pandemicTwoSnippet = snippetFactoryOlavo.makeSnippet(Method.POST, pandemicTwoJson, MatchAndTradeRestUtil.itemsUrl(tradeMembershipIdOlavo) + "/");
		pandemicTwoSnippet.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, OWNER_ITEM_TWO, pandemicTwoSnippet.asHtml());
		pandemicTwoJson = JsonUtil.fromResponse(pandemicTwoSnippet.getResponse(), ItemJson.class);
		
		// MEMBER SETUP
		SnippetFactory snippetFactoryMaria = new SnippetFactory(ContentType.JSON, MatchAndTradeRestUtil.nextAuthorizationHeader());
		MatchAndTradeApiFacade matchAndTradeApiFacadeMaria = new MatchAndTradeApiFacade(MatchAndTradeRestUtil.getLastAuthorizationHeader());

		// Building a user with a given user name for documentation clarity
		UserJson userMaria = MatchAndTradeRestUtil.getLastAuthenticatedUser();
		userMaria.setName("Maria");
		matchAndTradeApiFacadeMaria.saveUser(userMaria);
		
		// MEMBER_AUTHENTICATE
		Snippet authenticateSnippetMaria = snippetFactoryMaria.makeSnippet(MatchAndTradeRestUtil.authenticateUrl());
		authenticateSnippetMaria.getRequest().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_AUTHENTICATE, authenticateSnippetOlavo.asHtml());

		// MEMBER_AUTHENTICATIONS
		Snippet authenticationsSnippetMaria = snippetFactoryMaria.makeSnippet(MatchAndTradeRestUtil.authenticationsUrl() + "/");
		authenticationsSnippetMaria.getRequest().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_AUTHENTICATIONS, authenticationsSnippetMaria.asHtml());

		// MEMBER_TRADES_MEMBERSHIP
		TradeMembershipJson tradeMembershipJsonMaria = new TradeMembershipJson();
		tradeMembershipJsonMaria.setTradeId(tradeJson.getTradeId());
		tradeMembershipJsonMaria.setUserId(userMaria.getUserId());
		Snippet tradeMembershipSnippetMaria = snippetFactoryMaria.makeSnippet(Method.POST, tradeMembershipJsonMaria, MatchAndTradeRestUtil.tradeMembershipsUrl() + "/");
		tradeMembershipSnippetMaria.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_TRADES_MEMBERSHIP, tradeMembershipSnippetMaria.asHtml());
		
		// MEMBER_ITEM_ONE
		Integer tradeMembershipIdMaria = matchAndTradeApiFacadeMaria.findTradeMembershipByUserIdAndTradeId(userMaria.getUserId(), tradeJson.getTradeId()).getTradeMembershipId();
		ItemJson stoneAgeJson = new ItemJson();
		stoneAgeJson.setName("Stone Age");
		Snippet stoneAgeSnippet = snippetFactoryMaria.makeSnippet(Method.POST, stoneAgeJson, MatchAndTradeRestUtil.itemsUrl(tradeMembershipIdMaria) + "/");
		stoneAgeSnippet.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_ITEM_ONE, stoneAgeSnippet.asHtml());
		stoneAgeJson = JsonUtil.fromResponse(stoneAgeSnippet.getResponse(), ItemJson.class);

		// MEMBER_ITEM_TWO
		ItemJson carcassonneJson = new ItemJson();
		carcassonneJson.setName("Carcassonne");
		Snippet carcassonneSnippet = snippetFactoryMaria.makeSnippet(Method.POST, carcassonneJson, MatchAndTradeRestUtil.itemsUrl(tradeMembershipIdMaria) + "/");
		carcassonneSnippet.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_ITEM_TWO, carcassonneSnippet.asHtml());

		// MEMBER_ITEM_THREE
		ItemJson noThanksJson = new ItemJson();
		noThanksJson.setName("No Thanks!");
		Snippet noThanksSnippet = snippetFactoryMaria.makeSnippet(Method.POST, noThanksJson, MatchAndTradeRestUtil.itemsUrl(tradeMembershipIdMaria) + "/");
		noThanksSnippet.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_ITEM_THREE, noThanksSnippet.asHtml());

		// TRADE_MATCHING_ITEMS
		tradeJson.setState(TradeJson.State.MATCHING_ITEMS);
		Integer tradeId = tradeJson.getTradeId();
		tradeJson.setTradeId(null); // Set as null because we do not want to display in the documentation
		tradeJson.setLinks(null); // Set as null because we do not want to display in the documentation
		Snippet tradePutOwner = snippetFactoryOlavo.makeSnippet(Method.PUT, tradeJson, MatchAndTradeRestUtil.tradesUrl(tradeId));
		tradePutOwner.getResponse().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, TRADE_MATCHING_ITEMS, tradePutOwner.asHtml());
		
		// Olavo offers Pandemic Legacy: Season 1 and wants Maria's Stone Age in exchange.
		template = offer(snippetFactoryOlavo, tradeMembershipIdOlavo, pandemicOneJson, stoneAgeJson, template, OWNER_OFFER_ONE);
		
		// Maria offers Stone Age and wants Olavo's Pandemic Legacy: Season 1 in exchange.
		template = offer(snippetFactoryMaria, tradeMembershipIdMaria, stoneAgeJson, pandemicOneJson, template, MEMBER_OFFER_ONE);
		
		// Maria offers Stone Age and wants Olavo's Pandemic Legacy: Season 2 in exchange.
		template = offer(snippetFactoryMaria, tradeMembershipIdMaria, stoneAgeJson, pandemicTwoJson, template, MEMBER_OFFER_TWO);
		
		// TRADE_GENERATE_TRADES
		tradeJson.setState(TradeJson.State.GENERATE_RESULTS);
		Snippet generateResultsSnippet = snippetFactoryOlavo.makeSnippet(Method.PUT, tradeJson, MatchAndTradeRestUtil.tradesUrl(tradeId));
		generateResultsSnippet.getResponse().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, TRADE_GENERATE_TRADES, generateResultsSnippet.asHtml());		
		
		// TRADE_RESULTS
		RequestSpecification plainTextRequestSpecification = snippetFactoryOlavo.getDefaultRequestSpecification();
		plainTextRequestSpecification.contentType(ContentType.TEXT);
		Snippet tradeResultsSnippet = SnippetFactory.makeSnippet(Method.GET, plainTextRequestSpecification, MatchAndTradeRestUtil.tradeResultsUrl(tradeId));
		template = TemplateUtil.replacePlaceholder(template, TRADE_RESULTS, tradeResultsSnippet.asHtml());
		
		return TemplateUtil.appendHeaderAndFooter(template);
	}

	private String offer(
			SnippetFactory snippetFactory,
			Integer tradeMembershipId,
			ItemJson offeredItem,
			ItemJson wantedItem,
			String template,
			String templatePlaceHolder) {
		OfferJson offerJson = new OfferJson();
		offerJson.setOfferedArticleId(offeredItem.getArticleId());
		offerJson.setWantedArticleId(wantedItem.getArticleId());
		Snippet snippet = snippetFactory.makeSnippet(
				Method.POST,
				offerJson,
				MatchAndTradeRestUtil.offerUrl(tradeMembershipId) + "/"
				);
		snippet.getResponse().then().statusCode(201);
		return TemplateUtil.replacePlaceholder(template, templatePlaceHolder, snippet.asHtml());
	}

}
