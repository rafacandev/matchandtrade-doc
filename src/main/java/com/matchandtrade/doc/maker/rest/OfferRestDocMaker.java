package com.matchandtrade.doc.maker.rest;

import java.util.Date;

import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.rest.v1.json.ItemJson;
import com.matchandtrade.rest.v1.json.OfferJson;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;
import com.matchandtrade.rest.v1.json.UserJson;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Method;


public class OfferRestDocMaker implements RestDocMaker {
	
	private static final String OFFERS_POST = "OFFERS_POST";
	private static final String OFFERS_GET = "OFFERS_GET";
	private static final String OFFERS_GET_ALL = "OFFERS_GET_ALL";


	@Override
	public String contentFilePath() {
		return "offers.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());

		// Trade Owner 'Olavo'
		SnippetFactory snippetFactoryOlavo = new SnippetFactory(ContentType.JSON, MatchAndTradeRestUtil.nextAuthorizationHeader());
		MatchAndTradeApiFacade apiFacadeOlavo = new MatchAndTradeApiFacade(MatchAndTradeRestUtil.getLastAuthenticatedUser(), MatchAndTradeRestUtil.getLastAuthorizationHeader());
		UserJson olavo = apiFacadeOlavo.getUser();
		olavo.setName("Olavo");
		apiFacadeOlavo.saveUser(olavo);
		TradeJson trade = apiFacadeOlavo.createTrade("Board games in Brasilia - " + new Date());
		
		// ITEM: Pandemic Legacy: Season 1
		Integer tradeMembershipIdOlavo = apiFacadeOlavo.findTradeMembershipByUserIdAndTradeId(olavo.getUserId(), trade.getTradeId()).getTradeMembershipId();
		ItemJson pandemicOne = new ItemJson();
		pandemicOne.setName("Pandemic Legacy: Season 1");
		Snippet pandemicOneSnippet = snippetFactoryOlavo.makeSnippet(Method.POST, pandemicOne, MatchAndTradeRestUtil.itemsUrl(tradeMembershipIdOlavo) + "/");
		pandemicOne = pandemicOneSnippet.getResponse().body().as(ItemJson.class);

		// Trade Member 'Maria'
		Header mariaHeader = MatchAndTradeRestUtil.nextAuthorizationHeader();
		MatchAndTradeApiFacade apiFacadeMaria = new MatchAndTradeApiFacade(MatchAndTradeRestUtil.getLastAuthenticatedUser(), mariaHeader);

		// Building a user with a given user name for documentation clarity
		UserJson userMaria = apiFacadeMaria.getUser();
		userMaria.setName("Maria");
		apiFacadeMaria.saveUser(userMaria);

		// Maria's membership
		TradeMembershipJson tradeMembershipJsonMaria = apiFacadeMaria.subscribeToTrade(trade);
		ItemJson stoneAge = apiFacadeMaria.createItem(tradeMembershipJsonMaria, "Stone Age");
		
		// Make offers
		OfferJson pandemicOneForStoneAge = new OfferJson();
		pandemicOneForStoneAge.setOfferedItemId(pandemicOne.getItemId());
		pandemicOneForStoneAge.setWantedItemId(stoneAge.getItemId());
		Snippet pandemicOneForStoneAgeSnippet = snippetFactoryOlavo.makeSnippet(Method.POST, pandemicOneForStoneAge, MatchAndTradeRestUtil.offerUrl(tradeMembershipIdOlavo) + "/");
		pandemicOneForStoneAgeSnippet.getResponse().then().statusCode(201);
		pandemicOneForStoneAge = pandemicOneForStoneAgeSnippet.getResponse().body().as(OfferJson.class);
		template = TemplateUtil.replacePlaceholder(template, OFFERS_POST, pandemicOneForStoneAgeSnippet.asHtml());

		apiFacadeMaria.createOffer(tradeMembershipJsonMaria.getTradeMembershipId(), stoneAge.getItemId(), pandemicOne.getItemId());
		
		Snippet getSnippet = snippetFactoryOlavo.makeSnippet(MatchAndTradeRestUtil.offerUrl(tradeMembershipIdOlavo, pandemicOneForStoneAge.getOfferId()));
		getSnippet.getResponse().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, OFFERS_GET, getSnippet.asHtml());

		
		return TemplateUtil.appendHeaderAndFooter(template);
	}

}
