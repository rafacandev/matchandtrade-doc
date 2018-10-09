package com.matchandtrade.doc.maker.rest;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.github.rafasantos.restdocmaker.util.JsonUtil;
import com.matchandtrade.doc.maker.TemplateHelper;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.rest.v1.json.MembershipJson;
import com.matchandtrade.rest.v1.json.TradeJson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


public class MembershipRestDocMaker implements RestDocMaker {
	
	private static final String MEMBERSHIPS_POST_PLACEHOLDER = "MEMBERSHIPS_POST_PLACEHOLDER";
	private static final String MEMBERSHIPS_GET_PLACEHOLDER = "MEMBERSHIPS_GET_PLACEHOLDER";
	private static final String MEMBERSHIPS_GET_ALL_PLACEHOLDER = "MEMBERSHIPS_GET_ALL_PLACEHOLDER";
	private static final String MEMBERSHIPS_SEARCH_PLACEHOLDER = "MEMBERSHIPS_SEARCH_PLACEHOLDER";
	private static final String MEMBERSHIPS_DELETE_PLACEHOLDER = "MEMBERSHIPS_DELETE_PLACEHOLDER";

	@Override
	public String contentFilePath() {
		return "memberships.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());

		// MEMBERSHIPS_POST_PLACEHOLDER
		SpecificationParser postMembershipParser = parserPostMembership();
		template = TemplateHelper.replacePlaceholder(template, MEMBERSHIPS_POST_PLACEHOLDER, postMembershipParser.asHtmlSnippet());
		MembershipJson membership = JsonUtil.fromResponse(postMembershipParser.getResponse(), MembershipJson.class);

		// MEMBERSHIPS_GET_PLACEHOLDER
		SpecificationParser getMembershipParser = parseGetMembership(membership);
		template = TemplateHelper.replacePlaceholder(template, MEMBERSHIPS_GET_PLACEHOLDER, getMembershipParser.asHtmlSnippet());

		// MEMBERSHIPS_GET_ALL_PLACEHOLDER
		SpecificationParser getAllMembershipsParser = parseGetAllMemberships();
		template = TemplateHelper.replacePlaceholder(template, MEMBERSHIPS_GET_ALL_PLACEHOLDER, getAllMembershipsParser.asHtmlSnippet());

		// MEMBERSHIPS_SEARCH_PLACEHOLDER
		SpecificationParser searchMembershipParser = parseSearchMembership();
		template = TemplateHelper.replacePlaceholder(template, MEMBERSHIPS_SEARCH_PLACEHOLDER, searchMembershipParser.asHtmlSnippet());

		// MEMBERSHIPS_DELETE_PLACEHOLDER
		SpecificationParser deleteMembershipParser = parseDeleteMembership(membership);
		template = TemplateHelper.replacePlaceholder(template, MEMBERSHIPS_DELETE_PLACEHOLDER, deleteMembershipParser.asHtmlSnippet());

		template = PaginationTemplateUtil.replacePaginationRows(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

	private SpecificationParser parseDeleteMembership(MembershipJson membership) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
			.delete(MatchAndTradeRestUtil.membershipsUrl(membership.getMembershipId()));
		parser.getResponse().then().statusCode(204);
		return parser;
	}

	private SpecificationParser parseSearchMembership() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
				.queryParam("userId", MatchAndTradeRestUtil.getLastAuthenticatedUserId())
				.get(MatchAndTradeRestUtil.membershipsUrl());
		return parser;
	}

	private SpecificationParser parseGetAllMemberships() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given().filter(filter)
			.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
			.queryParam("_pageNumber", "1")
			.queryParam("_pageSize", "3")
			.get(MatchAndTradeRestUtil.membershipsUrl());
		parser.getResponse().then().statusCode(200);
		return parser;
	}

	private SpecificationParser parseGetMembership(MembershipJson membership) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
			.get(MatchAndTradeRestUtil.membershipsUrl(membership.getMembershipId()));
		parser.getResponse().then().statusCode(200).and().body("membershipId", equalTo(membership.getMembershipId()));
		return parser;
	}

	private SpecificationParser parserPostMembership() {
		MembershipJson membership = buildMembership();
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
			.contentType(ContentType.JSON)
			.body(membership)
			.post(MatchAndTradeRestUtil.membershipsUrl() + "/");
		parser.getResponse().then().statusCode(201).and().body("membershipId", notNullValue());
		return parser;
	}

	private MembershipJson buildMembership() {
		MatchAndTradeApiFacade apiFacade = new MatchAndTradeApiFacade();
		TradeJson tradeJson = apiFacade.createTrade("Board games in Vancouver - " + System.currentTimeMillis());
		MatchAndTradeRestUtil.nextAuthorizationHeader();
		MembershipJson membership = new MembershipJson();
		membership.setTradeId(tradeJson.getTradeId());
		membership.setUserId(MatchAndTradeRestUtil.getLastAuthenticatedUserId());
		return membership;
	}

}
