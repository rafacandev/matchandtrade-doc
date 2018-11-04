package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.util.*;
import com.matchandtrade.rest.v1.json.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;


public class OfferDocument implements Document {
	
	private static final String OFFERS_POST = "OFFERS_POST";
	private static final String OFFERS_GET = "OFFERS_GET";
	private static final String OFFERS_SEARCH = "OFFERS_SEARCH";
	private static final String OFFERS_DELETE = "OFFERS_DELETE";

	private final MatchAndTradeClient ownerClientApi;
	private String template;
	private Header ownerAuthorizationHeader;

	public OfferDocument() {
		ownerAuthorizationHeader = MatchAndTradeRestUtil.nextAuthorizationHeader();
		ownerClientApi = new MatchAndTradeClient(ownerAuthorizationHeader);
		template = TemplateUtil.buildTemplate(contentFilePath());
	}

	@Override
	public String content() {
		// ### Setup a trade with an owner and a member so the can later make offers for their articles
		// Create a user named 'Olavo'
		UserJson olavo = ownerClientApi.findUser().getResponse().as(UserJson.class);
		MatchAndTradeApiFacade olavoApiFacade = new MatchAndTradeApiFacade(olavo, ownerAuthorizationHeader);
		olavo.setName("Olavo");
		ownerClientApi.update(olavo);

		// Olavo creates a trade
		TradeJson trade = new TradeJson();
		trade.setName("Board games in Brasilia - " + System.currentTimeMillis());
		trade = ownerClientApi.create(trade).getResponse().as(TradeJson.class);
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
		SpecificationParser postOfferParser = parsePostOffer(ownerAuthorizationHeader, olavoMembership, olavoPandemic.getArticleId(), mariaStoneAge.getArticleId());
		template = TemplateUtil.replacePlaceholder(template, OFFERS_POST, postOfferParser.asHtmlSnippet());
		OfferJson pandemicOneForStoneAge = postOfferParser.getResponse().body().as(OfferJson.class);

		// OFFERS_GET
		SpecificationParser getOfferById = parseGetOfferById(ownerAuthorizationHeader, olavoMembership, pandemicOneForStoneAge);
		template = TemplateUtil.replacePlaceholder(template, OFFERS_GET, getOfferById.asHtmlSnippet());

		// OFFERS_SEARCH
		SpecificationParser searchOffersParser = parseSearchOffers(ownerAuthorizationHeader, olavoMembership, olavoPandemic, mariaStoneAge);
		template = TemplateUtil.replacePlaceholder(template, OFFERS_SEARCH, searchOffersParser.asHtmlSnippet());

		// OFFERS_DELETE
		SpecificationParser deleteParser = parseDeleteOffer(ownerAuthorizationHeader, olavoMembership, pandemicOneForStoneAge);
		template = TemplateUtil.replacePlaceholder(template, OFFERS_DELETE, deleteParser.asHtmlSnippet());

		template = PaginationTemplateUtil.replacePaginationRows(template);
		return TemplateUtil.appendHeaderAndFooter(template);
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

	public static SpecificationParser parsePostOffer(
			Header authorizationHeader,
	        MembershipJson membership,
			Integer offeredArticleId,
			Integer wantedArticleId) {

		OfferJson offer = new OfferJson();
		offer.setOfferedArticleId(offeredArticleId);
		offer.setWantedArticleId(wantedArticleId);

		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(authorizationHeader)
			.contentType(ContentType.JSON)
			.body(offer)
			.post(MatchAndTradeRestUtil.offerUrl(membership.getMembershipId()) + "/");
		parser.getResponse().then().statusCode(201);
		return parser;
	}

}
