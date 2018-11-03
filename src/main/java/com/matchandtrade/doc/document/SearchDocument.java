
package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.rest.v1.json.ArticleJson;
import com.matchandtrade.rest.v1.json.MembershipJson;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.UserJson;
import com.matchandtrade.rest.v1.json.search.Matcher;
import com.matchandtrade.rest.v1.json.search.Operator;
import com.matchandtrade.rest.v1.json.search.Recipe;
import com.matchandtrade.rest.v1.json.search.SearchCriteriaJson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import java.util.Date;

import static org.hamcrest.Matchers.equalTo;


public class SearchDocument implements Document {
	
	private static final String SEARCH_POST_PLACEHOLDER = "SEARCH_POST_PLACEHOLDER";
	private static final String TRADE_ID_PLACEHOLDER = "TRADE_ID";
	private static final String MEMBER_ID_PLACEHOLDER = "MEMBERSHIP_ID";

	@Override
	public String contentFilePath() {
		return "search.html";
	}

	@Override
	public String content() {
		TradeJson trade = buildTrade();
		initOwnerData(trade);
		MembershipJson secondMember = initMemberData(trade);

		// SEARCH_POST_PLACEHOLDER
		SpecificationParser parser = parsePostSearch(trade, secondMember);
//
		String template = TemplateUtil.buildTemplate(contentFilePath());
		// TODO
		template = TemplateUtil.replacePlaceholder(template, TRADE_ID_PLACEHOLDER, trade.getTradeId().toString());
		template = TemplateUtil.replacePlaceholder(template, MEMBER_ID_PLACEHOLDER, secondMember.getMembershipId().toString());
		template = TemplateUtil.replacePlaceholder(template, SEARCH_POST_PLACEHOLDER, parser.asHtmlSnippet());
		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

	private SpecificationParser parsePostSearch(TradeJson trade, MembershipJson secondMember) {
		SearchCriteriaJson search = new SearchCriteriaJson();
		search.setRecipe(Recipe.ARTICLES);
		search.addCriterion("Trade.tradeId", trade.getTradeId());
		search.addCriterion("Membership.membershipId", secondMember.getMembershipId(), Operator.AND, Matcher.NOT_EQUALS);

		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
			.queryParam("_pageNumber", "2")
			.queryParam("_pageSize", "2")
			.contentType(ContentType.JSON)
			.body(search)
			.post(MatchAndTradeRestUtil.searchUrl());
		parser.getResponse().then().statusCode(200).and().header("X-Pagination-Total-Count", equalTo("5"));
		return parser;
	}

	private MembershipJson initMemberData(TradeJson trade) {
		MatchAndTradeRestUtil.nextAuthorizationHeader();
		UserJson user = MatchAndTradeRestUtil.getLastAuthenticatedUser();
		MatchAndTradeApiFacade apiFacade = new MatchAndTradeApiFacade(MatchAndTradeRestUtil.getLastAuthorizationHeader());
		MembershipJson membership = apiFacade.subscribeToTrade(user.getUserId(), trade.getTradeId());
		apiFacade.createArticle(membership, "DO NOT DISPLAY THIS ARTICLE");
		return membership;
	}

	private void initOwnerData(TradeJson trade) {
		MatchAndTradeRestUtil.nextAuthorizationHeader();
		UserJson user = MatchAndTradeRestUtil.getLastAuthenticatedUser();
		MatchAndTradeApiFacade apiFacade = new MatchAndTradeApiFacade(MatchAndTradeRestUtil.getLastAuthorizationHeader());
		MembershipJson memberMembership = apiFacade.subscribeToTrade(user.getUserId(), trade.getTradeId());
		ArticleJson elysiumArticle = apiFacade.createArticle(memberMembership, "Elysium");
		apiFacade.createListing(memberMembership.getMembershipId(), elysiumArticle.getArticleId());
		ArticleJson voyagesOfMarcoPoloArticle = apiFacade.createArticle(memberMembership, "The Voyages of Marco Polo");
		apiFacade.createListing(memberMembership.getMembershipId(), voyagesOfMarcoPoloArticle.getArticleId());
		ArticleJson deusArticle = apiFacade.createArticle(memberMembership, "Deus");
		apiFacade.createListing(memberMembership.getMembershipId(), deusArticle.getArticleId());
	}

	private TradeJson buildTrade() {
		UserJson user = MatchAndTradeRestUtil.getLastAuthenticatedUser();
		MatchAndTradeApiFacade apiFacade = new MatchAndTradeApiFacade();
		TradeJson trade = apiFacade.createTrade("Search Recipe ARTICLES - " + new Date().getTime() + hashCode());
		MembershipJson membership = apiFacade.findMembershipByUserIdAndTradeId(user.getUserId(), trade.getTradeId());
		ArticleJson imperialSettlersArticle = apiFacade.createArticle(membership, "Imperial Settlers");
		apiFacade.createListing(membership.getMembershipId(), imperialSettlersArticle.getArticleId());
		ArticleJson deadOfWinterArticle = apiFacade.createArticle(membership, "Dead of Winter: A Crossroads Game");
		apiFacade.createListing(membership.getMembershipId(), deadOfWinterArticle.getArticleId());
		return trade;
	}

}
