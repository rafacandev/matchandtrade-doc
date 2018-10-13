
package com.matchandtrade.doc.maker.rest;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.matchandtrade.doc.maker.TemplateHelper;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
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


public class SearchRestDocMaker implements RestDocMaker {
	
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
		buildFirstMember(trade);
		MembershipJson secondMember = buildSecondMember(trade);

		// SEARCH_POST_PLACEHOLDER
		SpecificationParser parser = parsePostSearch(trade, secondMember);

		String template = TemplateHelper.buildTemplate(contentFilePath());
		template = TemplateHelper.replacePlaceholder(template, TRADE_ID_PLACEHOLDER, trade.getTradeId().toString());
		template = TemplateHelper.replacePlaceholder(template, MEMBER_ID_PLACEHOLDER, secondMember.getMembershipId().toString());
		template = TemplateHelper.replacePlaceholder(template, SEARCH_POST_PLACEHOLDER, parser.asHtmlSnippet());
		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateHelper.appendHeaderAndFooter(template);
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

	private MembershipJson buildSecondMember(TradeJson trade) {
		MatchAndTradeRestUtil.nextAuthorizationHeader();
		UserJson anotherMember = MatchAndTradeRestUtil.getLastAuthenticatedUser();
		MatchAndTradeApiFacade apiAsAnotherMember = new MatchAndTradeApiFacade(MatchAndTradeRestUtil.getLastAuthorizationHeader());
		MembershipJson anotherMemberMembership = apiAsAnotherMember.subscribeToTrade(anotherMember.getUserId(), trade.getTradeId());
		apiAsAnotherMember.createArticle(anotherMemberMembership, "DO NOT DISPLAY THIS ARTICLE");
		return anotherMemberMembership;
	}

	private void buildFirstMember(TradeJson trade) {
		MatchAndTradeRestUtil.nextAuthorizationHeader();
		UserJson member = MatchAndTradeRestUtil.getLastAuthenticatedUser();
		MatchAndTradeApiFacade apiAsMember = new MatchAndTradeApiFacade(MatchAndTradeRestUtil.getLastAuthorizationHeader());
		MembershipJson memberMembership = apiAsMember.subscribeToTrade(member.getUserId(), trade.getTradeId());
		apiAsMember.createArticle(memberMembership, "Elysium");
		apiAsMember.createArticle(memberMembership, "The Voyages of Marco Polo");
		apiAsMember.createArticle(memberMembership, "Deus");
	}

	private TradeJson buildTrade() {
		UserJson owner = MatchAndTradeRestUtil.getLastAuthenticatedUser();
		MatchAndTradeApiFacade apiAsOwner = new MatchAndTradeApiFacade();
		TradeJson trade = apiAsOwner.createTrade("Search Recipe ARTICLES - " + new Date().getTime() + hashCode());
		MembershipJson membership = apiAsOwner.findMembershipByUserIdAndTradeId(owner.getUserId(), trade.getTradeId());
		apiAsOwner.createArticle(membership, "Imperial Settlers");
		apiAsOwner.createArticle(membership, "Dead of Winter: A Crossroads Game");
		return trade;
	}

}
