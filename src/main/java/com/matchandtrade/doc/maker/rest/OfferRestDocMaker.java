package com.matchandtrade.doc.maker.rest;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.maker.DocumentContent;
import com.matchandtrade.doc.maker.TemplateHelper;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.rest.v1.json.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;


public class OfferRestDocMaker implements DocumentContent {
	
	private static final String OFFERS_POST = "OFFERS_POST";
	private static final String OFFERS_GET = "OFFERS_GET";
	private static final String OFFERS_SEARCH = "OFFERS_SEARCH";
	private static final String OFFERS_DELETE = "OFFERS_DELETE";

	@Override
	public String content() {
		String template = TemplateHelper.buildTemplate(contentFilePath());

		// ### Setup a trade with an owner and a member so the can later make offers for their articles
		// Create a user named 'Olavo'
		Header olavoAuthorizationHeader = MatchAndTradeRestUtil.nextAuthorizationHeader();
		UserJson olavo = MatchAndTradeRestUtil.getLastAuthenticatedUser();
		MatchAndTradeApiFacade olavoApiFacade = new MatchAndTradeApiFacade(olavo, MatchAndTradeRestUtil.getLastAuthorizationHeader());
		olavo.setName("Olavo");
		olavoApiFacade.saveUser(olavo);

		// Olavo creates a trade
		TradeJson trade = olavoApiFacade.createTrade("Board games in Brasilia - " + System.currentTimeMillis());
		MembershipJson olavoMembership = olavoApiFacade.findMembershipByUserIdAndTradeId(olavo.getUserId(), trade.getTradeId());
		// Create another user named 'Maria'
		Header mariaAuthorizationHeader = MatchAndTradeRestUtil.nextAuthorizationHeader();
		UserJson maria = MatchAndTradeRestUtil.getLastAuthenticatedUser();
		MatchAndTradeApiFacade mariaApiFacade = new MatchAndTradeApiFacade(maria, mariaAuthorizationHeader);
		maria.setName("Maria");
		mariaApiFacade.saveUser(maria);
		// Maria subscribes to Olavo's trade
		MembershipJson mariaMembership = mariaApiFacade.subscribeToTrade(trade);
		// Create articles that will be used in offers
		ArticleJson olavoPandemic = olavoApiFacade.createArticle(olavoMembership, "Pandemic Legacy: Season 1");
		// List that article
		olavoApiFacade.createListing(olavoMembership.getMembershipId(), olavoPandemic.getArticleId());
		ArticleJson mariaStoneAge = mariaApiFacade.createArticle(mariaMembership, "Stone Age");
		mariaApiFacade.createListing(mariaMembership.getMembershipId(), mariaStoneAge.getArticleId());
		// End of setup

		// OFFERS_POST
		SpecificationParser postOfferParser = parsePostOffer(olavoAuthorizationHeader, olavoMembership, olavoPandemic.getArticleId(), mariaStoneAge.getArticleId());
		template = TemplateHelper.replacePlaceholder(template, OFFERS_POST, postOfferParser.asHtmlSnippet());
		OfferJson pandemicOneForStoneAge = postOfferParser.getResponse().body().as(OfferJson.class);

		// OFFERS_GET
		SpecificationParser getOfferById = parseGetOfferById(olavoAuthorizationHeader, olavoMembership, pandemicOneForStoneAge);
		template = TemplateHelper.replacePlaceholder(template, OFFERS_GET, getOfferById.asHtmlSnippet());

		// OFFERS_SEARCH
		SpecificationParser searchOffersParser = parseSearchOffers(olavoAuthorizationHeader, olavoMembership, olavoPandemic, mariaStoneAge);
		template = TemplateHelper.replacePlaceholder(template, OFFERS_SEARCH, searchOffersParser.asHtmlSnippet());

		// OFFERS_DELETE
		SpecificationParser deleteParser = parseDeleteOffer(olavoAuthorizationHeader, olavoMembership, pandemicOneForStoneAge);
		template = TemplateHelper.replacePlaceholder(template, OFFERS_DELETE, deleteParser.asHtmlSnippet());

		template = PaginationTemplateUtil.replacePaginationRows(template);
		return TemplateHelper.appendHeaderAndFooter(template);
	}

	@Override
	public String contentFilePath() {
		return "offers.html";
	}

	private SpecificationParser parseDeleteOffer(Header olavoAuthorizationHeader, MembershipJson olavoMembership, OfferJson pandemicOneForStoneAge) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser deleteParser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(olavoAuthorizationHeader)
			.delete(MatchAndTradeRestUtil.offerUrl(olavoMembership.getMembershipId(), pandemicOneForStoneAge.getOfferId()));
		deleteParser.getResponse().then().statusCode(204);
		return deleteParser;
	}

	private SpecificationParser parseSearchOffers(Header olavoAuthorizationHeader, MembershipJson olavoMembership, ArticleJson pandemicOne, ArticleJson stoneAge) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser searchOffersParser = new SpecificationParser(filter);
		RestAssured.given().filter(filter)
			.header(olavoAuthorizationHeader)
			.queryParam("_pageNumber", "1")
			.queryParam("_pageSize", "3")
			.queryParam("offeredArticleId", pandemicOne.getArticleId())
			.queryParam("wantedArticleId", stoneAge.getArticleId())
			.get(MatchAndTradeRestUtil.offerUrl(olavoMembership.getMembershipId()));
		searchOffersParser.getResponse().then().statusCode(200);
		return searchOffersParser;
	}

	private SpecificationParser parseGetOfferById(Header olavoAuthorizationHeader, MembershipJson olavoMembership, OfferJson pandemicOneForStoneAge) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(olavoAuthorizationHeader)
			.get(MatchAndTradeRestUtil.offerUrl(olavoMembership.getMembershipId(), pandemicOneForStoneAge.getOfferId()));
		parser.getResponse().then().statusCode(200);
		return parser;
	}

	private SpecificationParser parsePostOffer(Header olavoAuthorizationHeader, MembershipJson olavoMembership, Integer offeredArticleId, Integer wantedArticleId) {
		OfferJson offer = new OfferJson();
		offer.setOfferedArticleId(offeredArticleId);
		offer.setWantedArticleId(wantedArticleId);

		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(olavoAuthorizationHeader)
			.contentType(ContentType.JSON)
			.body(offer)
			.post(MatchAndTradeRestUtil.offerUrl(olavoMembership.getMembershipId()) + "/");
		parser.getResponse().then().statusCode(201);
		return parser;
	}

}
