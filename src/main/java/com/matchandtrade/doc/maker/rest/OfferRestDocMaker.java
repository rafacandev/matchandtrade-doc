package com.matchandtrade.doc.maker.rest;

import java.util.Date;

import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.rest.v1.json.ItemJson;
import com.matchandtrade.rest.v1.json.OfferJson;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;
import com.matchandtrade.rest.v1.json.UserJson;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;


public class OfferRestDocMaker implements RestDocMaker {
	
	private static final String OFFERS_POST = "OFFERS_POST";
	private static final String OFFERS_GET = "OFFERS_GET";
	private static final String OFFERS_SEARCH = "OFFERS_SEARCH";
	private static final String OFFERS_DELETE = "OFFERS_DELETE";


	@Override
	public String contentFilePath() {
		return "offers.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());

		// Trade Owner 'Olavo'
		SnippetFactory olavoSnippetFactory = new SnippetFactory(ContentType.JSON, MatchAndTradeRestUtil.nextAuthorizationHeader());
		MatchAndTradeApiFacade olavoApiFacade = new MatchAndTradeApiFacade(MatchAndTradeRestUtil.getLastAuthenticatedUser(), MatchAndTradeRestUtil.getLastAuthorizationHeader());
		UserJson olavo = olavoApiFacade.getUser();
		olavo.setName("Olavo");
		olavoApiFacade.saveUser(olavo);
		TradeJson trade = olavoApiFacade.createTrade("Board games in Brasilia - " + new Date().getTime() + hashCode());
		
		// ITEM: Pandemic Legacy: Season 1
		TradeMembershipJson olavoMembership = olavoApiFacade.findTradeMembershipByUserIdAndTradeId(olavo.getUserId(), trade.getTradeId());
		ItemJson pandemicOne = new ItemJson();
		pandemicOne.setName("Pandemic Legacy: Season 1");
		Snippet pandemicOneSnippet = olavoSnippetFactory.makeSnippet(Method.POST, pandemicOne, MatchAndTradeRestUtil.itemsUrl(olavoMembership.getTradeMembershipId()) + "/");
		pandemicOne = pandemicOneSnippet.getResponse().body().as(ItemJson.class);

		// Trade Member 'Maria'
		Header mariaAuthorizationHeader = MatchAndTradeRestUtil.nextAuthorizationHeader();
		MatchAndTradeApiFacade mariaApiFacade = new MatchAndTradeApiFacade(MatchAndTradeRestUtil.getLastAuthenticatedUser(), mariaAuthorizationHeader);

		// Building a user with a given user name for documentation clarity
		UserJson maria = mariaApiFacade.getUser();
		maria.setName("Maria");
		mariaApiFacade.saveUser(maria);

		// Maria's membership
		TradeMembershipJson mariaMembership = mariaApiFacade.subscribeToTrade(trade);
		ItemJson stoneAge = mariaApiFacade.createItem(mariaMembership, "Stone Age");
		
		// Make offers
		OfferJson pandemicOneForStoneAge = new OfferJson();
		pandemicOneForStoneAge.setOfferedArticleId(pandemicOne.getArticleId());
		pandemicOneForStoneAge.setWantedArticleId(stoneAge.getArticleId());
		Snippet pandemicOneForStoneAgeSnippet = olavoSnippetFactory.makeSnippet(Method.POST, pandemicOneForStoneAge, MatchAndTradeRestUtil.offerUrl(olavoMembership.getTradeMembershipId()) + "/");
		pandemicOneForStoneAgeSnippet.getResponse().then().statusCode(201);
		pandemicOneForStoneAge = pandemicOneForStoneAgeSnippet.getResponse().body().as(OfferJson.class);
		template = TemplateUtil.replacePlaceholder(template, OFFERS_POST, pandemicOneForStoneAgeSnippet.asHtml());

		mariaApiFacade.createOffer(mariaMembership.getTradeMembershipId(), stoneAge.getArticleId(), pandemicOne.getArticleId());
		
		Snippet getSnippet = olavoSnippetFactory.makeSnippet(MatchAndTradeRestUtil.offerUrl(olavoMembership.getTradeMembershipId(), pandemicOneForStoneAge.getOfferId()));
		getSnippet.getResponse().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, OFFERS_GET, getSnippet.asHtml());

		ItemJson firstDummy = mariaApiFacade.createItem(mariaMembership, "First ummy item so it displays in the GET ALL");
		ItemJson secondDummy = mariaApiFacade.createItem(mariaMembership, "Second dummy item so it displays in the GET ALL");
		olavoApiFacade.createOffer(olavoMembership.getTradeMembershipId(), pandemicOne.getArticleId(), firstDummy.getArticleId());
		olavoApiFacade.createOffer(olavoMembership.getTradeMembershipId(), pandemicOne.getArticleId(), secondDummy.getArticleId());
		
		RequestSpecification searchRequest = new RequestSpecBuilder()
				.addRequestSpecification(olavoSnippetFactory.getDefaultRequestSpecification())
				.addQueryParam("_pageNumber", "1")
				.addQueryParam("_pageSize", "3")
				.addQueryParam("offeredItemId", pandemicOne.getArticleId())
				.addQueryParam("wantedItemId", stoneAge.getArticleId())
				.build();
		Snippet searchSnippet = SnippetFactory.makeSnippet(Method.GET, searchRequest, MatchAndTradeRestUtil.offerUrl(olavoMembership.getTradeMembershipId()));
		searchSnippet.getResponse().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, OFFERS_SEARCH, searchSnippet.asHtml());

		Snippet deleteSnippet = olavoSnippetFactory.makeSnippet(Method.DELETE, MatchAndTradeRestUtil.offerUrl(olavoMembership.getTradeMembershipId(), pandemicOneForStoneAge.getOfferId()));
		deleteSnippet.getResponse().then().statusCode(204);
		template = TemplateUtil.replacePlaceholder(template, OFFERS_DELETE, deleteSnippet.asHtml());
		
		template = PaginationTemplateUtil.replacePaginationRows(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

}
