package com.matchandtrade.doc.maker.rest;

import org.apache.http.Header;

import com.matchandtrade.doc.maker.OutputMaker;
import com.matchandtrade.doc.util.JsonUtil;
import com.matchandtrade.doc.util.RequestResponseHolder;
import com.matchandtrade.doc.util.RequestResponseUtil;
import com.matchandtrade.doc.util.RestUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;


public class RestTradeMembershipMaker extends OutputMaker {
	
	public static final String BASE_URL = "/rest/v1/trade-memberships/";
	public static final String TRADES_MEMBERSHIP_POST_SNIPPET = "TRADES_MEMBERSHIP_POST_SNIPPET";
	public static final String TRADES_MEMBERSHIP_GET_SNIPPET = "TRADES_MEMBERSHIP_GET_SNIPPET";
	public static final String TRADES_MEMBERSHIP_GET_ALL_SNIPPET = "TRADES_MEMBERSHIP_GET_ALL_SNIPPET";
	public static final String TRADES_MEMBERSHIP_SEARCH_SNIPPET = "TRADES_MEMBERSHIP_SEARCH_SNIPPET";
	public static final String TRADES_MEMBERSHIP_DELETE_SNIPPET = "TRADES_MEMBERSHIP_DELETE_SNIPPET";
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());

		// TRADES_MEMBERSHIP_POST_SNIPPET
		RequestResponseHolder post = buildPostJson("Board games in Vancouver", null);
		TradeMembershipJson postJson = JsonUtil.fromHttpResponse(post.getHttpResponse(), TradeMembershipJson.class);
		String postSnippet = TemplateUtil.buildSnippet(post.getHttpRequest(), post.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_POST_SNIPPET, postSnippet);
		
		// TRADES_MEMBERSHIP_GET_SNIPPET
		TradeMembershipJson postResponseJson = JsonUtil.fromString(RestUtil.buildResponseBodyString(post.getHttpResponse()), TradeMembershipJson.class);
		RequestResponseHolder get = RequestResponseUtil.buildGetRequestResponse(BASE_URL + postResponseJson.getTradeMembershipId());
		String getSnippet = TemplateUtil.buildSnippet(get.getHttpRequest(), get.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_GET_SNIPPET, getSnippet);

		// TRADES_MEMBERSHIP_GET_ALL_SNIPPET
		RequestResponseHolder getAll = RequestResponseUtil.buildGetRequestResponse(BASE_URL);
		String getAllSnippet = TemplateUtil.buildSnippet(getAll.getHttpRequest(), getAll.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_GET_ALL_SNIPPET, getAllSnippet);

		// TRADES_MEMBERSHIP_SEARCH_SNIPPET
		RequestResponseHolder search = RequestResponseUtil.buildGetRequestResponse(
			BASE_URL+ "?userId="
			+ RestUtil.getAuthenticatedUser().getUserId()
			+ "&tradeId="+postJson.getTradeId()+"&_pageNumber=1&_pageSize=3");
		String searchSnippet = TemplateUtil.buildSnippet(search.getHttpRequest(), search.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_SEARCH_SNIPPET, searchSnippet);

		// TRADES_MEMBERSHIP_DELETE_SNIPPET
		RequestResponseHolder del = RequestResponseUtil.buildDeleteRequestResponse(BASE_URL + postResponseJson.getTradeMembershipId());
		String delSnippet = TemplateUtil.buildSnippet(del.getHttpRequest(), del.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_DELETE_SNIPPET, delSnippet);
		
		return template;
	}
	
	/**
	 * Creates a new Trade with the given {@code tradeName} and calls @{code buildPostJson(TradeJson, Header}
	 *
	 * @param tradeName
	 * @param newAuthenticationHeader
	 * @return
	 */
	public static RequestResponseHolder buildPostJson(String tradeName, Header newAuthenticationHeader) {
		TradeJson tradeJson = new TradeJson();
		tradeJson.setName(tradeName);
		RequestResponseHolder tradeRRH = RequestResponseUtil.buildPostRequestResponse("/rest/v1/trades/", tradeJson);
		TradeJson tradeJsonResponse = JsonUtil.fromString(RestUtil.buildResponseBodyString(tradeRRH.getHttpResponse()), TradeJson.class);
		return buildPostJson(tradeJsonResponse, newAuthenticationHeader);
	}

	/**
	 * Creates a new TradeMembership associated with the created Trade.
	 * The created TradeMembership is a <i>member</i>. Since Trades creates a TradeMembership <i>owner</i> by default,
	 * this method requires a <i>newAuthenticationHeader</i> to be used when creating the TradeMembership.
	 * You can pass null as <i>newAuthenticationHeader</i> which is going result in a new authentication.
	 *
	 * @param tradeJson
	 * @param newAuthenticationHeader
	 * @return
	 */
	public static RequestResponseHolder buildPostJson(TradeJson tradeJson, Header newAuthenticationHeader) {
		// Set authentication header
		RestUtil.setAuthenticationHeader(newAuthenticationHeader);
		// Create new TradeMembership, it is going to be member provided that newAuthenticationHeader is different from the current authentication header 
		TradeMembershipJson tradeMembershipJson = new TradeMembershipJson();
		tradeMembershipJson.setUserId(RestUtil.getAuthenticatedUser().getUserId());
		tradeMembershipJson.setTradeId(tradeJson.getTradeId());
		return RequestResponseUtil.buildPostRequestResponse(BASE_URL, tradeMembershipJson);
	}

	@Override
	public String getDocLocation() {
		return "trade-memberships.html";
	}

}
