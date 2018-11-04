package com.matchandtrade.doc.util;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.rest.v1.json.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;

public class MatchAndTradeClient {

	private final Header authorizationHeader;
	private Integer userId;
//	private final String cookie;

	public MatchAndTradeClient() {
		SpecificationParser parser = authenticate();
		String authorizationHeaderName = "Authorization";
		String authorizationHeaderValue = parser.getResponse().getHeader(authorizationHeaderName);
//		this.cookie = parser.getResponse().getCookie("MTSESSION");
		this.authorizationHeader = new Header(authorizationHeaderName, authorizationHeaderValue);

		SpecificationParser authenticationsParser = findAuthentications();
		userId = authenticationsParser.getResponse().body().path("userId");
	}

	public MatchAndTradeClient(Header authorizationHeader) {
		this.authorizationHeader = authorizationHeader;
		SpecificationParser authenticationsParser = findAuthentications();
		userId = authenticationsParser.getResponse().body().path("userId");
	}

	public static SpecificationParser authenticate() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.get(MatchAndTradeRestUtil.authenticateUrl());
		// Assert status is redirect
		parser.getResponse().then().statusCode(302);
		return parser;
	}

	public SpecificationParser create(ArticleJson article) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
				.given()
				.filter(filter)
				.contentType(ContentType.JSON)
				.header(authorizationHeader)
				.body(article)
				.post(MatchAndTradeRestUtil.articlesUrl() + "/");
		parser.getResponse().then().statusCode(201);
		return parser;
	}

	public SpecificationParser create(ListingJson listing) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
				.given()
				.filter(filter)
				.contentType(ContentType.JSON)
				.header(authorizationHeader)
				.body(listing)
				.post(MatchAndTradeRestUtil.listingUrl() + "/");
		parser.getResponse().then().statusCode(201);
		return parser;
	}

	public SpecificationParser create(TradeJson trade) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
			.given()
			.filter(filter)
			.header(getAuthorizationHeader())
			.contentType(ContentType.JSON)
			.body(trade)
			.post(MatchAndTradeRestUtil.tradesUrl() + "/");
		parser.getResponse().then().statusCode(201);
		return parser;
	}

	public SpecificationParser create(MembershipJson membership) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(authorizationHeader)
				.contentType(ContentType.JSON)
				.body(membership)
				.post(MatchAndTradeRestUtil.membershipsUrl() + "/");
		parser.getResponse().then().statusCode(201);
		return parser;
	}

	public static SpecificationParser findArticle(Integer articleId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
				.given()
				.filter(filter)
				.get(MatchAndTradeRestUtil.articlesUrl(articleId) + "/");
		parser.getResponse().then().statusCode(200);
		return parser;
	}

	public static SpecificationParser findArticles() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
				.given()
				.filter(filter)
				.get(MatchAndTradeRestUtil.articlesUrl());
		parser.getResponse().then().statusCode(200);
		return parser;
	}

	/**
	 * Need to keep the same cookie between "authenticate" and "authenticate info"
	 *
	 * @param cookie
	 * @return
	 */
	public SpecificationParser findAuthenticationInfo(String cookie) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.header("cookie", cookie)
				.filter(filter)
				.get(MatchAndTradeRestUtil.authenticateInfoUrl());
		parser.getResponse().then().statusCode(200);
		return parser;
	}

	public SpecificationParser findAuthentications() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(getAuthorizationHeader())
			.get(MatchAndTradeRestUtil.authenticationsUrl() + "/");
		parser.getResponse().then().statusCode(200).and().body("", hasKey("userId"));
		return parser;
	}

	public SpecificationParser findMembership(Integer membershipId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
				.get(MatchAndTradeRestUtil.membershipsUrl(membershipId));
		parser.getResponse().then().statusCode(200);
		return parser;
	}

	public SpecificationParser findMemberships(Integer pageNumber, Integer pageSize) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(authorizationHeader)
				.contentType(ContentType.JSON)
				.param("_pageNumber", pageNumber)
				.param("_pageSize", pageSize)
				.get(MatchAndTradeRestUtil.membershipsUrl());
		parser.getResponse().then().statusCode(200);
		return parser;
	}

	public SpecificationParser findMembershipByUserIdOrTradeId(Integer userId, Integer tradeId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RequestSpecification request = RestAssured.given()
				.filter(filter)
				.header(authorizationHeader)
				.contentType(ContentType.JSON);
		if (userId != null) {
			request.queryParam("userId", userId);
		}
		if (userId != null) {
			request.queryParam("tradeId", tradeId);
		}
		request.get(MatchAndTradeRestUtil.membershipsUrl());

		parser.getResponse().then().statusCode(200);
		return parser;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MembershipJson findMembershipByUserIdOrTradeIdAsMembership(Integer userId, Integer tradeId) {
		SpecificationParser parser = findMembershipByUserIdOrTradeId(userId, tradeId);
		List<Map> responseList = parser.getResponse().body().as(List.class);
		Map<String, Object> membershipAsMap = responseList.get(0);
		MembershipJson result = new MembershipJson();
		result.setUserId(userId);
		result.setTradeId(tradeId);
		result.setMembershipId(Integer.parseInt(membershipAsMap.get("membershipId").toString()));
		result.setType(MembershipJson.Type.valueOf(membershipAsMap.get("type").toString()));
		return result;
	}

	public SpecificationParser findTrade(Integer tradeId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(authorizationHeader)
				.get(MatchAndTradeRestUtil.tradesUrl(tradeId));
		parser.getResponse().then().statusCode(200).and().body("tradeId", equalTo(tradeId));
		return parser;
	}

	public SpecificationParser deleteArticle(Integer articleId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
				.given()
				.filter(filter)
				.header(authorizationHeader)
				.delete(MatchAndTradeRestUtil.articlesUrl(articleId));
		parser.getResponse().then().statusCode(204);
		return parser;
	}

	public SpecificationParser deleteListing(ListingJson listing) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
				.given()
				.filter(filter)
				.contentType(ContentType.JSON)
				.header(authorizationHeader)
				.body(listing)
				.delete(MatchAndTradeRestUtil.listingUrl() + "/");
		parser.getResponse().then().statusCode(204);
		return parser;
	}

	public SpecificationParser deleteMembership(Integer membershipId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(authorizationHeader)
				.delete(MatchAndTradeRestUtil.membershipsUrl(membershipId));
		parser.getResponse().then().statusCode(204);
		return parser;
	}

	public SpecificationParser deleteTrade(Integer tradeId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(authorizationHeader)
				.delete(MatchAndTradeRestUtil.tradesUrl(tradeId));
		parser.getResponse().then().statusCode(204);
		return parser;
	}

	public static SpecificationParser findTrades(Integer pageNumber, Integer pageSize) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		Response response = RestAssured.given()
				.filter(filter)
				.contentType(ContentType.JSON)
				.param("_pageNumber", pageNumber)
				.param("_pageSize", pageSize)
				.get(MatchAndTradeRestUtil.tradesUrl());
		response.then().statusCode(200).and().body("[0].tradeId", notNullValue());
		return parser;
	}

	public static SpecificationParser findTrades() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		Response response = RestAssured.given()
				.filter(filter)
				.contentType(ContentType.JSON)
				.get(MatchAndTradeRestUtil.tradesUrl());
		response.then().statusCode(200);
		return parser;
	}

	public SpecificationParser findUser() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
			.given()
			.filter(filter)
			.header(authorizationHeader)
			.get(MatchAndTradeRestUtil.usersUrl(userId));
		return parser;
	}

	public Header getAuthorizationHeader() {
		return authorizationHeader;
	}

	public Integer getUserId() {
		return userId;
	}

	public SpecificationParser update(ArticleJson article) {
		ArticleJson requestBody = new ArticleJson();
		BeanUtils.copyProperties(article, requestBody);
		requestBody.setArticleId(null);

		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
				.given()
				.filter(filter)
				.contentType(ContentType.JSON)
				.header(authorizationHeader)
				.body(article)
				.put(MatchAndTradeRestUtil.articlesUrl(article.getArticleId()));
		parser.getResponse().then().statusCode(200);
		return parser;
	}

	public SpecificationParser update(TradeJson trade) {
		TradeJson requestBody = new TradeJson();
		BeanUtils.copyProperties(trade, requestBody);
		requestBody.setTradeId(null);

		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
				.given()
				.filter(filter)
				.header(getAuthorizationHeader())
				.contentType(ContentType.JSON)
				.body(requestBody)
				.put(MatchAndTradeRestUtil.tradesUrl(trade.getTradeId()));
		parser.getResponse().then().statusCode(200);
		return parser;
	}

	public SpecificationParser update(UserJson user) {
		UserJson requestBody = new UserJson();
		BeanUtils.copyProperties(user, requestBody);
		user.setUserId(null);

		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
				.contentType(ContentType.JSON)
				.body(user)
				.put(MatchAndTradeRestUtil.usersUrl(userId));
		parser.getResponse().then().statusCode(200).and().body("name", equalTo(user.getName()));
		return parser;
	}

	public SpecificationParser singOff() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
				.get(MatchAndTradeRestUtil.signOffUrl());
		parser.getResponse().then().statusCode(205).header("Authorization", nullValue());
		return parser;
	}

}
