package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.MembershipJson;
import com.matchandtrade.rest.v1.json.TradeJson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


public class MembershipDocument implements Document {
	
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
		template = TemplateUtil.replacePlaceholder(template, MEMBERSHIPS_POST_PLACEHOLDER, postMembershipParser.asHtmlSnippet());
		MembershipJson membership = postMembershipParser.getResponse().body().as(MembershipJson.class);

		// MEMBERSHIPS_GET_PLACEHOLDER
		SpecificationParser getMembershipParser = MembershipDocument.parseGetMembership(membership);
		template = TemplateUtil.replacePlaceholder(template, MEMBERSHIPS_GET_PLACEHOLDER, getMembershipParser.asHtmlSnippet());

		// MEMBERSHIPS_GET_ALL_PLACEHOLDER
		SpecificationParser getAllMembershipsParser = parseGetAllMemberships();
		template = TemplateUtil.replacePlaceholder(template, MEMBERSHIPS_GET_ALL_PLACEHOLDER, getAllMembershipsParser.asHtmlSnippet());

		// MEMBERSHIPS_SEARCH_PLACEHOLDER
		SpecificationParser searchMembershipParser = buildSearchMembershipParser(
				MatchAndTradeRestUtil.getLastAuthenticatedUserId(),
				null,
				MatchAndTradeRestUtil.getLastAuthorizationHeader());
		template = TemplateUtil.replacePlaceholder(template, MEMBERSHIPS_SEARCH_PLACEHOLDER, searchMembershipParser.asHtmlSnippet());

		// MEMBERSHIPS_DELETE_PLACEHOLDER
		SpecificationParser deleteMembershipParser = parseDeleteMembership(membership);
		template = TemplateUtil.replacePlaceholder(template, MEMBERSHIPS_DELETE_PLACEHOLDER, deleteMembershipParser.asHtmlSnippet());

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

	public static SpecificationParser buildSearchMembershipParser(Integer userId, Integer tradeId, Header authorizationHeader) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RequestSpecification request = RestAssured.given()
				.filter(filter)
				.header(authorizationHeader)
				.queryParam("userId", userId);
		if (tradeId != null) {
			request.queryParam("tradeId", tradeId);
		}
		request.get(MatchAndTradeRestUtil.membershipsUrl());
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

	public static SpecificationParser parseGetMembership(MembershipJson membership) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
			.get(MatchAndTradeRestUtil.membershipsUrl(membership.getMembershipId()));
		parser.getResponse().then().statusCode(200).and().body("membershipId", equalTo(membership.getMembershipId()));
		return parser;
	}

	public static SpecificationParser parseGetMembership(Integer membershipId) {
		MembershipJson request = new MembershipJson();
		request.setMembershipId(membershipId);
		return parseGetMembership(request);
	}

	private SpecificationParser parserPostMembership() {
		MembershipJson membership = buildMembership();
		return parserPostMembership(membership);
	}

	public static SpecificationParser parserPostMembership(MembershipJson membership) {
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
