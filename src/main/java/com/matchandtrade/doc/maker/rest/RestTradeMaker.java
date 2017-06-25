package com.matchandtrade.doc.maker.rest;

import com.matchandtrade.doc.maker.OutputMaker;
import com.matchandtrade.doc.util.JsonUtil;
import com.matchandtrade.doc.util.RequestResponseHolder;
import com.matchandtrade.doc.util.RestUtil;
import com.matchandtrade.doc.util.RequestResponseUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeJson.State;


public class RestTradeMaker extends OutputMaker {
	
	public static final String BASE_URL = "/rest/v1/trades";
	public static final String TRADES_POST_SNIPPET = "TRADES_POST_SNIPPET";
	private static final String TRADES_PUT_SNIPPET = "TRADES_PUT_SNIPPET";	
	private static final String TRADES_GET_SNIPPET = "TRADES_GET_SNIPPET";
	private static final String TRADES_DELETE_SNIPPET = "TRADES_DELETE_SNIPPET";
	private static final String TRADES_SEARCH_SNIPPET = "TRADES_SEARCH_SNIPPET";
	private static final String TRADES_GET_ALL_SNIPPET = "TRADES_GET_ALL_SNIPPET";
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());

		// TRADES_POST_SNIPPET
		TradeJson tradeJson = new TradeJson();
		tradeJson.setName("Board games");
		RequestResponseHolder post = RequestResponseUtil.buildPostRequestResponse(BASE_URL + "/", tradeJson);
		String postSnippet = TemplateUtil.buildSnippet(post.getHttpRequest(), post.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_POST_SNIPPET, postSnippet);
		tradeJson = JsonUtil.fromString(RestUtil.buildResponseBodyString(post.getHttpResponse()), TradeJson.class);
		
		// TRADES_PUT_SNIPPET
		tradeJson.setName("Board games in Toronto");
		tradeJson.setState(State.MATCHING_ITEMS);
		Integer tradeId = tradeJson.getTradeId();
		tradeJson.setTradeId(null); // Set as null because we do not want the id to be displayed on the request body to emphasize that the id must be sent on the URL 
		RequestResponseHolder put = RequestResponseUtil.buildPutRequestResponse(BASE_URL + "/" + tradeId, tradeJson);
		String putSnippet = TemplateUtil.buildSnippet(put.getHttpRequest(), put.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_PUT_SNIPPET, putSnippet);
		
		// TRADES_GET_SNIPPET
		TradeJson postResponse = JsonUtil.fromString(RestUtil.buildResponseBodyString(post.getHttpResponse()), TradeJson.class);
		RequestResponseHolder get = RequestResponseUtil.buildGetRequestResponse(BASE_URL + "/" + postResponse.getTradeId());
		String getSnippet = TemplateUtil.buildSnippet(get.getHttpRequest(), get.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_GET_SNIPPET, getSnippet);

		// TRADES_SEARCH_SNIPPET
		RequestResponseHolder search = RequestResponseUtil.buildGetRequestResponse(BASE_URL + "?name=Board%20games%20in%20Toronto&_pageNumber=1&_pageSize=2");
		String searchSnippet = TemplateUtil.buildSnippet(search.getHttpRequest(), search.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_SEARCH_SNIPPET, searchSnippet);

		// TRADES_GET_ALL_SNIPPET
		RequestResponseHolder getAll = RequestResponseUtil.buildGetRequestResponse(BASE_URL + "/");
		String getAllSnippet = TemplateUtil.buildSnippet(getAll.getHttpRequest(), getAll.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_GET_ALL_SNIPPET, getAllSnippet);
		
		// TRADES_DELETE_SNIPPET
		RequestResponseHolder del = RequestResponseUtil.buildDeleteRequestResponse(BASE_URL + "/" + postResponse.getTradeId());
		String delSnippet = TemplateUtil.buildSnippet(del.getHttpRequest(), del.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_DELETE_SNIPPET, delSnippet);
		
		return template;
	}

	@Override
	public String getDocLocation() {
		return "trades.html";
	}

}
