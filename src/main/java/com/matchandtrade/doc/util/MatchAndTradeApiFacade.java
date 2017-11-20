package com.matchandtrade.doc.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.rafasantos.restdocmaker.util.JsonUtil;
import com.matchandtrade.rest.v1.json.ItemJson;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class MatchAndTradeApiFacade {
	
	private Map<String, String> defaultHeaders = new HashMap<>();
	
	public MatchAndTradeApiFacade() {
		defaultHeaders = MatchAndTradeRestUtil.getLastAuthorizationHeaderAsMap();
	}
	public MatchAndTradeApiFacade(Header... defaultHeaders) {
		List<Header> headersList = Arrays.asList(defaultHeaders);
		this.defaultHeaders = headersList.stream().collect(Collectors.toMap(Header::getName, Header::getValue));
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
		return JsonUtil.fromHttpResponse(response, TradeJson.class);
	}

	public TradeMembershipJson subscribeToTrade(Integer userId, Integer tradeId) {
		TradeMembershipJson requestBody = new TradeMembershipJson();
		requestBody.setUserId(userId);
		requestBody.setTradeId(tradeId);
		Response response = RestAssured
				.given()
				.headers(defaultHeaders)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post(MatchAndTradeRestUtil.tradeMembershipsUrl() + "/");
		return JsonUtil.fromHttpResponse(response, TradeMembershipJson.class);
	}

	public TradeMembershipJson findTradeMembershipByUserIdAndTradeId(Integer userId, Integer tradeId) {
		RequestSpecification request = new RequestSpecBuilder()
				.addHeaders(defaultHeaders)
				.addParam("userId", userId)
				.addParam("tradeId", tradeId)
				.build();
		Response response = RestAssured
				.given()
				.spec(request)
				.when()
				.get(MatchAndTradeRestUtil.tradeMembershipsUrl());
		@SuppressWarnings({ "rawtypes", "unchecked" })
		List<Map> responseList = response.body().as(List.class);
		String tradeMembershipAsString = JsonUtil.toJson(responseList.get(0));
		return JsonUtil.fromString(tradeMembershipAsString, TradeMembershipJson.class);
	}

	public ItemJson createItem(TradeMembershipJson tradeMembershipJson, String itemName) {
		ItemJson requestBody = new ItemJson();
		requestBody.setName(itemName);
		Response response = RestAssured
				.given()
				.headers(defaultHeaders)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post(MatchAndTradeRestUtil.itemsUrl(tradeMembershipJson.getTradeMembershipId()) + "/");
		return response.body().as(ItemJson.class);
	}
	
}
