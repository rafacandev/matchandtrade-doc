package com.matchandtrade.doc.util;

import java.util.HashMap;
import java.util.Map;

import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.util.JsonUtil;
import com.matchandtrade.doc.config.PropertiesLoader;
import com.matchandtrade.rest.v1.json.UserJson;

import io.restassured.http.Header;

public class MatchAndTradeRestUtil {
	
	private static String baseUrl = PropertiesLoader.serverUrl();
	private static Header lastAuthorizationHeader;
	private static final SnippetFactory snippetFactory = new SnippetFactory();
	
	private enum Endpoint {
		AUTHENTICATE("authenticate"),
		SIGN_OFF(AUTHENTICATE.getPath() + "/sign-out"),
		AUTHENTICATIONS("rest/v1/authentications"),
		TRADES("rest/v1/trades"),
		TRADE_RESULTS("results"),
		ITEMS("items"),
		WANT_ITEMS("want-items"),
		TRADE_MEMBERSHIPS("rest/v1/trade-memberships"),
		USERS("rest/v1/users");

		private String path;
		
		private Endpoint(String path) {
			this.path = path;
		}
		
		public String getPath() {
			return path;
		}
		
		public String asURL(String baseUrl) {
			return baseUrl + "/" + path;
		}
	}
	
	public static String authenticateUrl() {
		return Endpoint.AUTHENTICATE.asURL(baseUrl);
	}
	
	public static String authenticationsUrl() {
		return Endpoint.AUTHENTICATIONS.asURL(baseUrl);
	}

	public static String signOffUrl() {
		return Endpoint.SIGN_OFF.asURL(baseUrl);
	}
	public static String usersUrl() {
		return Endpoint.USERS.asURL(baseUrl);
	}
	public static String tradesUrl() {
		return Endpoint.TRADES.asURL(baseUrl);
	}
	public static String tradesUrl(Integer tradeId) {
		return Endpoint.TRADES.asURL(baseUrl) + "/" + tradeId;
	}
	public static String wantItemsUrl(Integer tradeMembershipId, Integer itemId) {
		return Endpoint.TRADE_MEMBERSHIPS.asURL(baseUrl) + "/" + tradeMembershipId + "/" + Endpoint.ITEMS.path + "/" + itemId + "/" + Endpoint.WANT_ITEMS.path;
	}
	public static String wantItemsUrl(Integer tradeMembershipId, Integer itemId, Integer wantItemId) {
		return Endpoint.TRADE_MEMBERSHIPS.asURL(baseUrl) + "/" + tradeMembershipId + "/" + Endpoint.ITEMS.path + "/" + itemId + "/" + Endpoint.WANT_ITEMS.path + "/" + wantItemId;
	}
	public static String itemsUrl(Integer tradeMembershipId, Integer itemId) {
		return Endpoint.TRADE_MEMBERSHIPS.asURL(baseUrl) + "/" + tradeMembershipId + "/" + Endpoint.ITEMS.path + "/" + itemId;
	}
	public static String itemsUrl(Integer tradeMembershipId) {
		return Endpoint.TRADE_MEMBERSHIPS.asURL(baseUrl) + "/" + tradeMembershipId + "/" + Endpoint.ITEMS.path;
	}
	public static String tradeMembershipsUrl() {
		return Endpoint.TRADE_MEMBERSHIPS.asURL(baseUrl);
	}
	public static String tradeMembershipsUrl(Integer tradeMembershipId) {
		return Endpoint.TRADE_MEMBERSHIPS.asURL(baseUrl) + "/" + tradeMembershipId;
	}
	public static String tradeResultsUrl(Integer tradeId) {
		return Endpoint.TRADES.asURL(baseUrl) + "/" + tradeId + "/" + Endpoint.TRADE_RESULTS.path;
	}


	
	public static Header getLastAuthorizationHeader() {
		if (lastAuthorizationHeader == null) {
			return nextAuthorizationHeader();
		} else {
			return lastAuthorizationHeader;
		}
	}
	
	public static Map<String, String> getLastAuthorizationHeaderAsMap() {
		Map<String, String> result = new HashMap<>();
		Header authorizationHeader = getLastAuthorizationHeader();
		result.put(authorizationHeader.getName(), authorizationHeader.getValue());
		return result;
	}

	public static Integer getLastAuthenticatedUserId() {
		SnippetFactory snippetFactory = new SnippetFactory(getLastAuthorizationHeader());
		Snippet snippet = snippetFactory.makeSnippet(authenticationsUrl() + "/");
		return snippet.getResponse().body().path("userId");
	}

	public static UserJson getLastAuthenticatedUser() {
		SnippetFactory snippetFactory = new SnippetFactory(getLastAuthorizationHeader());
		Snippet snippet = snippetFactory.makeSnippet(usersUrl() + "/" + getLastAuthenticatedUserId());
		snippet.getResponse().then().statusCode(200);
		return JsonUtil.fromString(snippet.getResponse().asString(), UserJson.class);
	}
	
	public static Header nextAuthorizationHeader() {
		Snippet authenticateSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.authenticateUrl());
		String headerValue = authenticateSnippet.getResponse().getHeader("Authorization");
		lastAuthorizationHeader = new Header("Authorization", headerValue);
		return lastAuthorizationHeader;
	}

}
