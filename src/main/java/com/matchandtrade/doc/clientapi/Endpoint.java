package com.matchandtrade.doc.clientapi;

import com.matchandtrade.doc.config.PropertiesLoader;

public class Endpoint {

	private static final String BASE_URL = PropertiesLoader.serverUrl() + "/matchandtrade-api/v1";

	public static String authenticateInfo() {
		return String.format("%s/%s", authenticate(), "info");
	}

	public static String authenticate() {
		return String.format("%s/%s", BASE_URL, "authenticate");
	}

	public static String authentications() {
		return String.format("%s/%s/", BASE_URL, "authentications");
	}

	public static String articles() {
		return String.format("%s/%s/", BASE_URL, "articles");
	}

	public static String articles(Integer articleId) {
		return String.format("%s%s", articles(), articleId);
	}

	public static String articleAttachments(Integer articleId, Integer attachmentId) {
		return String.format("%s/attachments/%s", articles(articleId), attachmentId);
	}

	public static String articleAttachments(Integer articleId) {
		return String.format("%s/attachments/", articles(articleId));
	}

	public static String listing() {
		return String.format("%s/%s/", BASE_URL, "listing");
	}

	public static String memberships() {
		return String.format("%s/%s/", BASE_URL, "memberships");
	}

	public static String memberships(Integer membershipId) {
		return String.format("%s%s", memberships(), membershipId);
	}

	public static String offers(Integer membershipId) {
		return String.format("%s/%s/", memberships(membershipId), "offers");
	}

	public static String offers(Integer membershipId, Integer offerId) {
		return String.format("%s%s", offers(membershipId), offerId);
	}

	public static String search() {
		return String.format("%s/%s", BASE_URL, "search");
	}

	public static String signOff() {
		return String.format("%s/%s", authenticate(), "sign-off");
	}

	public static String trades() {
		return String.format("%s/%s/", BASE_URL, "trades");
	}

	public static String tradeResults(Integer tradeId) {
		return String.format("%s/%s/", trades(tradeId), "results");
	}

	public static String trades(Integer tradeId) {
		return String.format("%s%s", trades(), tradeId);
	}

	public static String users(Integer userId) {
		return String.format("%s/%s/%s", BASE_URL, "users", userId);
	}

}
