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


public class RestWantItemMaker implements OutputMaker {
	
	public static final String BASE_URL = "/want-items";
	public static final String WANT_ITEMS_POST_SNIPPET = "WANT_ITEMS_POST_SNIPPET";
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		
		// Create a trade membership
		TradeJson tradeJson = new TradeJson();
		tradeJson.setName("Board games in Calgary");
		RequestResponseHolder tradeRRH = RequestResponseUtil.buildPostRequestResponse(RestTradeMaker.BASE_URL, tradeJson);
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
		
		// Alpha wants beta
		String url = RestTradeMembershipMaker.BASE_URL + tradeMembershipAlphaJson.getTradeMembershipId() + RestItemMaker.BASE_URL + "/" + itemAlphaJson.getItemId() + BASE_URL;
		WantItemJson wantItemJson = new WantItemJson();
		wantItemJson.setPriority(1);
		wantItemJson.setItem(itemBetaJson);
		RequestResponseHolder wantItemRRH = RequestResponseUtil.buildPostRequestResponse(url, wantItemJson);
		String wantItemSnippet = TemplateUtil.buildSnippet(wantItemRRH.getHttpRequest(), wantItemRRH.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, WANT_ITEMS_POST_SNIPPET, wantItemSnippet);
		return template;
	}

	@Override
	public String getDocLocation() {
		return "doc/rest/want-items.md";
	}

}
