package com.matchandtrade.doc.maker.rest;

import com.matchandtrade.doc.maker.OutputMaker;
import com.matchandtrade.doc.util.JsonUtil;
import com.matchandtrade.doc.util.RequestResponseHolder;
import com.matchandtrade.doc.util.RequestResponseUtil;
import com.matchandtrade.doc.util.RestUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.ItemJson;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;
import com.matchandtrade.rest.v1.json.WantItemJson;


public class RestWantItemMaker extends OutputMaker {
	
	public static final String BASE_URL = "/want-items";
	public static final String WANT_ITEMS_POST_SNIPPET = "WANT_ITEMS_POST_SNIPPET";
	public static final String WANT_ITEMS_GET_SNIPPET = "WANT_ITEMS_GET_SNIPPET";
	public static final String WANT_ITEMS_GET_ALL_SNIPPET = "WANT_ITEMS_GET_ALL_SNIPPET";
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		
		// Create a trade membership
		TradeJson tradeJson = new TradeJson();
		tradeJson.setName("Board games in Calgary");
		RequestResponseHolder tradeRRH = RequestResponseUtil.buildPostRequestResponse(RestTradeMaker.BASE_URL + "/", tradeJson);
		tradeJson = JsonUtil.fromString(RestUtil.buildResponseBodyString(tradeRRH.getHttpResponse()), TradeJson.class);
		
		// TradeMembership alpha
		RequestResponseHolder tradeMembershipAlpha = RestTradeMembershipMaker.buildPostJson(tradeJson, null);
		TradeMembershipJson tradeMembershipAlphaJson = JsonUtil.fromHttpResponse(tradeMembershipAlpha.getHttpResponse(), TradeMembershipJson.class);
		// Create item alpha
		ItemJson itemAlphaJson = new ItemJson();
		itemAlphaJson.setName("Libertalia");
		RequestResponseHolder itemAlphaRRH = RequestResponseUtil.buildPostRequestResponse(RestTradeMembershipMaker.BASE_URL + tradeMembershipAlphaJson.getTradeMembershipId() + RestItemMaker.BASE_URL, itemAlphaJson);
		itemAlphaJson = JsonUtil.fromHttpResponse(itemAlphaRRH.getHttpResponse(), ItemJson.class);

		// TradeMembership beta
		RequestResponseHolder tradeMembershipBeta = RestTradeMembershipMaker.buildPostJson(tradeJson, null);
		TradeMembershipJson tradeMembershipBetaJson = JsonUtil.fromHttpResponse(tradeMembershipBeta.getHttpResponse(), TradeMembershipJson.class);
		// Create item beta
		ItemJson itemBetaJson = new ItemJson();
		itemBetaJson.setName("Libertalia");
		RequestResponseHolder itemBetaRRH = RequestResponseUtil.buildPostRequestResponse(RestTradeMembershipMaker.BASE_URL + tradeMembershipBetaJson.getTradeMembershipId() + RestItemMaker.BASE_URL, itemBetaJson);
		itemBetaJson = JsonUtil.fromHttpResponse(itemBetaRRH.getHttpResponse(), ItemJson.class);
		
		// WANT_ITEMS_POST_SNIPPET: alpha wants beta
		String url = RestTradeMembershipMaker.BASE_URL + tradeMembershipAlphaJson.getTradeMembershipId() + RestItemMaker.BASE_URL + "/" + itemAlphaJson.getItemId() + BASE_URL;
		WantItemJson wantItemJson = new WantItemJson();
		wantItemJson.setPriority(1);
		wantItemJson.setItemId(itemBetaJson.getItemId());
		RequestResponseHolder wantItemRRH = RequestResponseUtil.buildPostRequestResponse(url, wantItemJson);
		wantItemJson = JsonUtil.fromHttpResponse(wantItemRRH.getHttpResponse(), WantItemJson.class);
		String wantItemSnippet = TemplateUtil.buildSnippet(wantItemRRH.getHttpRequest(), wantItemRRH.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, WANT_ITEMS_POST_SNIPPET, wantItemSnippet);
		
		// WANT_ITEMS_GET_SNIPPET
		RequestResponseHolder wantItemGetRRH = RequestResponseUtil.buildGetRequestResponse(url + "/" + wantItemJson.getWantItemId());
		String wantItemGetSnippet = TemplateUtil.buildSnippet(wantItemGetRRH.getHttpRequest(), wantItemGetRRH.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, WANT_ITEMS_GET_SNIPPET, wantItemGetSnippet);

		// WANT_ITEMS_GET_ALL_SNIPPET
		RequestResponseHolder wantItemGetAllRRH = RequestResponseUtil.buildGetRequestResponse(url + "?_pageNumber=1&_pageSize=3");
		String wantItemGetAllSnippet = TemplateUtil.buildSnippet(wantItemGetAllRRH.getHttpRequest(), wantItemGetAllRRH.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, WANT_ITEMS_GET_ALL_SNIPPET, wantItemGetAllSnippet);
		
		return template;
	}

	@Override
	public String getDocLocation() {
		return "want-items.html";
	}

}
