package com.matchandtrade.doc.maker.rest;

import com.matchandtrade.doc.maker.OutputMaker;
import com.matchandtrade.doc.util.JsonUtil;
import com.matchandtrade.doc.util.RequestResponseHolder;
import com.matchandtrade.doc.util.RestUtil;
import com.matchandtrade.doc.util.SnippetUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;


public class RestTradeMembershipsMaker implements OutputMaker {
	
	public static final String BASE_URL = "/rest/v1/trade-memberships/";
	public static final String TRADES_MEMBERSHIP_POST_SNIPPET = "TRADES_MEMBERSHIP_POST_SNIPPET";
	public static final String TRADES_MEMBERSHIP_GET_SNIPPET = "TRADES_MEMBERSHIP_GET_SNIPPET";
	public static final String TRADES_MEMBERSHIP_GET_ALL_SNIPPET = "TRADES_MEMBERSHIP_GET_ALL_SNIPPET";
	public static final String TRADES_MEMBERSHIP_SEARCH_SNIPPET = "TRADES_MEMBERSHIP_SEARCH_SNIPPET";
	public static final String TRADES_MEMBERSHIP_DELETE_SNIPPET = "TRADES_MEMBERSHIP_DELETE_SNIPPET";
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		
		RequestResponseHolder post = buildPostJson("Board games in New York");
		TradeMembershipJson postJson = JsonUtil.fromHttpResponse(post.getHttpResponse(), TradeMembershipJson.class);
		String postSnippet = TemplateUtil.buildSnippet(post.getHttpRequest(), post.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_POST_SNIPPET, postSnippet);
		
		TradeMembershipJson postResponseJson = JsonUtil.fromString(RestUtil.buildResponseBodyString(post.getHttpResponse()), TradeMembershipJson.class);
		RequestResponseHolder get = SnippetUtil.buildGetRequestResponse(BASE_URL + postResponseJson.getTradeMembershipId());
		String getSnippet = TemplateUtil.buildSnippet(get.getHttpRequest(), get.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_GET_SNIPPET, getSnippet);

		RequestResponseHolder getAll = SnippetUtil.buildGetRequestResponse(BASE_URL);
		String getAllSnippet = TemplateUtil.buildSnippet(getAll.getHttpRequest(), getAll.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_GET_ALL_SNIPPET, getAllSnippet);

		RequestResponseHolder search = SnippetUtil.buildGetRequestResponse(
			BASE_URL+ "?userId="
			+ RestUtil.getAuthenticatedUser().getUserId()
			+ "&tradeId="+postJson.getTradeId()+"&_pageNumber=1&_pageSize=10");
		String searchSnippet = TemplateUtil.buildSnippet(search.getHttpRequest(), search.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_SEARCH_SNIPPET, searchSnippet);

		RequestResponseHolder del = SnippetUtil.buildDeleteRequestResponse(BASE_URL + postResponseJson.getTradeMembershipId());
		String delSnippet = TemplateUtil.buildSnippet(del.getHttpRequest(), del.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_DELETE_SNIPPET, delSnippet);
		
		return template;
	}
	
	public static RequestResponseHolder buildPostJson(String tradeName) {
		// Create a new trade
		TradeJson trade = new TradeJson();
		trade.setName(tradeName);
		RequestResponseHolder tradeRRH = SnippetUtil.buildPostRequestResponse("/rest/v1/trades/", trade);
		trade = JsonUtil.fromString(RestUtil.buildResponseBodyString(tradeRRH.getHttpResponse()), TradeJson.class);
		// Set authentication header as null to force to authenticate as a new user because the previous user is already the owner of the previous trade
		RestUtil.setAuthenticationHeader(null);
		// Become member of one of the created trade
		TradeMembershipJson postJson = new TradeMembershipJson();
		postJson.setUserId(RestUtil.getAuthenticatedUser().getUserId());
		postJson.setTradeId(trade.getTradeId());
		return SnippetUtil.buildPostRequestResponse(BASE_URL, postJson);
	}

	@Override
	public String getDocLocation() {
		return "doc/rest/trade-memberships.md";
	}

}
