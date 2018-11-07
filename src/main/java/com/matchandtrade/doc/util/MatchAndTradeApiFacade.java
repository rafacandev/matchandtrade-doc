package com.matchandtrade.doc.util;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.matchandtrade.doc.document.AttachmentDocument;
import com.matchandtrade.rest.v1.json.*;
import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MatchAndTradeApiFacade {
	
	private Map<String, String> defaultHeaders;
	private UserJson user;
	
	public MatchAndTradeApiFacade() {
		defaultHeaders = MatchAndTradeRestUtil.getLastAuthorizationHeaderAsMap();
		user = MatchAndTradeRestUtil.getLastAuthenticatedUser();
	}
	public MatchAndTradeApiFacade(Header... defaultHeaders) {
		List<Header> headersList = Arrays.asList(defaultHeaders);
		this.defaultHeaders = headersList.stream().collect(Collectors.toMap(Header::getName, Header::getValue));
	}

	public MatchAndTradeApiFacade(UserJson user, Header... defaultHeaders) {
		List<Header> headersList = Arrays.asList(defaultHeaders);
		this.defaultHeaders = headersList.stream().collect(Collectors.toMap(Header::getName, Header::getValue));
		this.user = user;
	}
	
	public ArticleJson createArticle(MembershipJson membershipJson, String articleName) {
		ArticleJson requestBody = new ArticleJson();
		requestBody.setName(articleName);
		Response response = RestAssured
				.given()
				.headers(defaultHeaders)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post(MatchAndTradeRestUtil.articlesUrl() + "/");
		return response.body().as(ArticleJson.class);
	}

	public ArticleJson createArticle(String articleName) {
		SpecificationFilter filter = new SpecificationFilter();
		ArticleJson requestBody = new ArticleJson();
		requestBody.setName(articleName);
		Response response = RestAssured
				.given()
				.filter(filter)
				.headers(defaultHeaders)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post(MatchAndTradeRestUtil.articlesUrl() + "/");
		return response.body().as(ArticleJson.class);
	}

	public OfferJson createOffer(Integer membershipId, Integer offeredArticleId, Integer wantedArticleId) {
		OfferJson requestBody = new OfferJson();
		requestBody.setOfferedArticleId(offeredArticleId);
		requestBody.setWantedArticleId(wantedArticleId);
		Response response = RestAssured
				.given()
				.headers(defaultHeaders)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post(MatchAndTradeRestUtil.offerUrl(membershipId) + "/");
		return response.body().as(OfferJson.class);
	}

	public TradeJson createTrade(String name) {
		TradeJson tradeJson = new TradeJson();
		tradeJson.setName(name);
		Response response = RestAssured
				.given()
				.headers(defaultHeaders)
				.contentType(ContentType.JSON)
				.body(tradeJson)
				.when()
				.post(MatchAndTradeRestUtil.tradesUrl() + "/");
		return response.body().as(TradeJson.class);
	}

	public MembershipJson findMembershipByUserIdAndTradeId(Integer userId, Integer tradeId) {
		RequestSpecification request = new RequestSpecBuilder()
				.addHeaders(defaultHeaders)
				.addParam("userId", userId)
				.addParam("tradeId", tradeId)
				.build();
		Response response = RestAssured
				.given()
				.spec(request)
				.when()
				.get(MatchAndTradeRestUtil.membershipsUrl());
		@SuppressWarnings({ "rawtypes", "unchecked" })
		List<Map> responseList = response.body().as(List.class);
		Map<String, Object> membershipAsMap = responseList.get(0);
		return obtainMembership(membershipAsMap, userId, tradeId);
	}

	private MembershipJson obtainMembership(Map<String, Object> membershipAsMap, Integer userId, Integer tradeId) {
		MembershipJson result = new MembershipJson();
		result.setUserId(userId);
		result.setTradeId(tradeId);
		int membershipId = Integer.parseInt(membershipAsMap.get("membershipId").toString());
		result.setMembershipId(membershipId);
		String type = membershipAsMap.get("type").toString();
		result.setType(MembershipJson.Type.valueOf(type));
		return result;
	}

	public UserJson getUser() {
		return user;
	}
	
	public UserJson saveUser(UserJson user) {
		Response response = RestAssured
				.given()
				.headers(defaultHeaders)
				.contentType(ContentType.JSON)
				.body(user)
				.when()
				.put(MatchAndTradeRestUtil.usersUrl(user.getUserId()));
		return response.body().as(UserJson.class);
	}

	public TradeJson saveTrade(TradeJson trade) {
		Response response = RestAssured
				.given()
				.headers(defaultHeaders)
				.contentType(ContentType.JSON)
				.body(trade)
				.when()
				.put(MatchAndTradeRestUtil.tradesUrl(trade.getTradeId()));
		return response.body().as(TradeJson.class);
	}
	
	public MembershipJson subscribeToTrade(Integer userId, Integer tradeId) {
		MembershipJson requestBody = new MembershipJson();
		requestBody.setUserId(userId);
		requestBody.setTradeId(tradeId);
		Response response = RestAssured
				.given()
				.headers(defaultHeaders)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post(MatchAndTradeRestUtil.membershipsUrl() + "/");
		return response.body().as(MembershipJson.class);
	}

	public MembershipJson subscribeToTrade(TradeJson trade) {
		MembershipJson requestBody = new MembershipJson();
		requestBody.setTradeId(trade.getTradeId());
		requestBody.setUserId(user.getUserId());
		Response response = RestAssured
			.given()
			.headers(defaultHeaders)
			.contentType(ContentType.JSON)
			.body(requestBody)
			.when()
			.post(MatchAndTradeRestUtil.membershipsUrl() + "/");
		return response.body().as(MembershipJson.class);
	}
	
	public int createListing(Integer membershipId, Integer articleId) {
		SpecificationFilter filter = new SpecificationFilter();
		ListingJson request = new ListingJson(membershipId, articleId);
		Response response = RestAssured
			.given()
			.filter(filter)
			.headers(defaultHeaders)
			.contentType(ContentType.JSON)
			.body(request)
			.when()
			.post(MatchAndTradeRestUtil.listingUrl(membershipId, articleId));
		return  response.statusCode();
	}

}
