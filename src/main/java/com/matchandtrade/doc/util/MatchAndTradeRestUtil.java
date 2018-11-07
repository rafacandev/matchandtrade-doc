package com.matchandtrade.doc.util;

import com.matchandtrade.doc.config.PropertiesLoader;
import com.matchandtrade.rest.v1.json.UserJson;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class MatchAndTradeRestUtil {
	
	private static String baseUrl = PropertiesLoader.serverUrl();
	private static Header lastAuthorizationHeader;

	private enum Endpoint {
		ARTICLES("matchandtrade-api/v1/articles"),
		ARTICLE_ATTACHMENTS("attachments"),
		AUTHENTICATE("matchandtrade-api/v1/authenticate"),
		AUTHENTICATIONS("matchandtrade-api/v1/authentications"),
		LISTING("matchandtrade-api/v1/listing"),
		SIGN_OFF("matchandtrade-api/v1/sign-out"),
		TRADES("matchandtrade-api/v1/trades"),
		ATTACHMENTS("matchandtrade-api/v1/attachments"),
		SEARCH("matchandtrade-api/v1/search"),
		TRADE_RESULTS("results"),
		INFO("info"),
		WANT_ARTICLES("want-articles"),
		MEMBERSHIPS("matchandtrade-api/v1/memberships"),
		OFFERS("offers"),
		USERS("matchandtrade-api/v1/users");

		private String path;
		
		private Endpoint(String path) {
			this.path = path;
		}
		
		public String asURL(String baseUrl) {
			return baseUrl + "/" + path;
		}
	}

	public static String deleteArticleAttachment(Integer articleId, Integer attachmentId) {
		return Endpoint.ARTICLES.asURL(baseUrl) + "/" + articleId + "/attachments/" + attachmentId;
	}

	public static String findArticleAttachments(Integer articleId, Integer attachmentId) {
		return Endpoint.ATTACHMENTS.asURL(baseUrl) + "/" + articleId + "/" + attachmentId;
	}


	public static String createAttachment() {
		return Endpoint.ATTACHMENTS.asURL(baseUrl);
	}


	public static String findArticleAttachments(Integer articleId) {
		return Endpoint.ARTICLES.asURL(baseUrl) + "/" + articleId + "/attachments";
	}


	public static String authenticateUrl() {
		return Endpoint.AUTHENTICATE.asURL(baseUrl);
	}

	public static String authenticateInfoUrl() {
		return Endpoint.AUTHENTICATE.asURL(baseUrl) + "/" + Endpoint.INFO.path;
	}
	
	public static String authenticationsUrl() {
		return Endpoint.AUTHENTICATIONS.asURL(baseUrl);
	}

	public static String attachmentsUrl(Integer attachmentId) {
		return Endpoint.ATTACHMENTS.asURL(baseUrl) + "/" + attachmentId;
	}


	public static String signOffUrl() {
		return Endpoint.AUTHENTICATE.asURL(baseUrl) + "/" + Endpoint.SIGN_OFF.path;
	}
	public static String searchUrl() {
		return Endpoint.SEARCH.asURL(baseUrl);
	}
	public static String usersUrl() {
		return Endpoint.USERS.asURL(baseUrl);
	}
	public static String usersUrl(Integer userId) {
		return Endpoint.USERS.asURL(baseUrl) + "/" + userId;
	}
	public static String tradesUrl() {
		return Endpoint.TRADES.asURL(baseUrl);
	}
	public static String articleAttachmentsUrl() {
		return Endpoint.ARTICLES.asURL(baseUrl) + "/";
	}

	public static String articleAttachmentsUrl(Integer articleId, Integer attachmentId) {
		return Endpoint.ARTICLES.asURL(baseUrl) + "/" + articleId + "/attachments/" + attachmentId;
	}


	public static String articleAttachmentsUrl(Integer membershipId, Integer articleId, Integer attachmentId) {
		return Endpoint.MEMBERSHIPS.asURL(baseUrl) + "/" + membershipId + "/articles/" + articleId + "/" + Endpoint.ARTICLE_ATTACHMENTS.path + "/" + attachmentId;
	}
	public static String articleAttachmentsUrl(Integer articleId) {
		return Endpoint.ARTICLES.asURL(baseUrl) +"/" + articleId + "/attachments/";
	}
	public static String ttachmentsUrl(Integer attachmentId) {
		return Endpoint.ATTACHMENTS.asURL(baseUrl) +"/" + attachmentId;
	}
	public static String tradesUrl(Integer tradeId) {
		return Endpoint.TRADES.asURL(baseUrl) + "/" + tradeId;
	}
	public static String wantArticlesUrl(Integer membershipId, Integer articleId) {
		return Endpoint.MEMBERSHIPS.asURL(baseUrl) + "/" + membershipId + "/" + Endpoint.ARTICLES.path + "/" + articleId + "/" + Endpoint.WANT_ARTICLES.path;
	}
	public static String wantArticlesUrl(Integer membershipId, Integer articleId, Integer wantArticleId) {
		return Endpoint.MEMBERSHIPS.asURL(baseUrl) + "/" + membershipId + "/" + Endpoint.ARTICLES.path + "/" + articleId + "/" + Endpoint.WANT_ARTICLES.path + "/" + wantArticleId;
	}
	public static String articlesUrl(Integer membershipId, Integer articleId) {
		return Endpoint.MEMBERSHIPS.asURL(baseUrl) + "/" + membershipId + "/" + Endpoint.ARTICLES.path + "/" + articleId;
	}
	public static String articlesUrl() {
		return Endpoint.ARTICLES.asURL(baseUrl);
	}
	public static String articlesUrl(Integer articleId) {
		return Endpoint.ARTICLES.asURL(baseUrl) + "/" + articleId;
	}
	public static String membershipsUrl() {
		return Endpoint.MEMBERSHIPS.asURL(baseUrl);
	}
	public static String membershipsUrl(Integer membershipId) {
		return Endpoint.MEMBERSHIPS.asURL(baseUrl) + "/" + membershipId;
	}
	public static String tradeResultsUrl(Integer tradeId) {
		return Endpoint.TRADES.asURL(baseUrl) + "/" + tradeId + "/" + Endpoint.TRADE_RESULTS.path;
	}
	public static String offerUrl(Integer membershipId) {
		return Endpoint.MEMBERSHIPS.asURL(baseUrl) + "/" + membershipId + "/" + Endpoint.OFFERS.path;
	}
	public static String offerUrl(Integer membershipId, Integer offerId) {
		return Endpoint.MEMBERSHIPS.asURL(baseUrl) + "/" + membershipId + "/" + Endpoint.OFFERS.path + "/" + offerId;
	}
	public static String listingUrl(Integer membershipId, Integer articleId) {
		return Endpoint.LISTING.asURL(baseUrl) + "/";
	}
	public static String listingUrl() {
		return Endpoint.LISTING.asURL(baseUrl);
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
		Response response = RestAssured
			.given()
			.header(getLastAuthorizationHeader())
			.get(authenticationsUrl() + "/");
		return response.body().path("userId");
	}

	public static UserJson getLastAuthenticatedUser() {
		Response response = RestAssured
				.given()
				.header(getLastAuthorizationHeader())
				.get(usersUrl() + "/" + getLastAuthenticatedUserId());
		response.then().statusCode(200);
		return response.body().as(UserJson.class);
	}
	
	public static Header nextAuthorizationHeader() {
		Response response = RestAssured
				.given()
				.get(MatchAndTradeRestUtil.authenticateUrl());
		String authorizationHeader = response.getHeader("Authorization");
		lastAuthorizationHeader = new Header("Authorization", authorizationHeader);
		return lastAuthorizationHeader;
	}

}
