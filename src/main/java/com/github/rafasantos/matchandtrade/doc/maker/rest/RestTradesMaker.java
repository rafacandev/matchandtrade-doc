package com.github.rafasantos.matchandtrade.doc.maker.rest;

import com.github.rafasantos.matchandtrade.doc.maker.OutputMaker;
import com.github.rafasantos.matchandtrade.doc.util.JsonUtil;
import com.github.rafasantos.matchandtrade.doc.util.RequestResponseHolder;
import com.github.rafasantos.matchandtrade.doc.util.RestUtil;
import com.github.rafasantos.matchandtrade.doc.util.SnippetUtil;
import com.github.rafasantos.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.TradeJson;


public class RestTradesMaker implements OutputMaker {
	
	public static final String BASE_URL = "/rest/v1/trades/";
	public static final String TRADES_POST_SNIPPET = "TRADES_POST_SNIPPET";
	private static final String TRADES_PUT_SNIPPET = "TRADES_PUT_SNIPPET";	
	private static final String TRADES_GET_SNIPPET = "TRADES_GET_SNIPPET";
	private static final String TRADES_DELETE_SNIPPET = "TRADES_DELETE_SNIPPET";
	private static final String TRADES_SEARCH_SNIPPET = "TRADES_SEARCH_SNIPPET";
	private static final String TRADES_GET_ALL_SNIPPET = "TRADES_GET_ALL_SNIPPET";
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());

		TradeJson firstTradeJson = new TradeJson();
		firstTradeJson.setName("Scott");
		RequestResponseHolder post = SnippetUtil.buildPostRequestResponse(BASE_URL, firstTradeJson);
		String postSnippet = TemplateUtil.buildSnippet(post.getHttpRequest(), post.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_POST_SNIPPET, postSnippet);
		firstTradeJson = JsonUtil.fromString(RestUtil.buildResponseBodyString(post.getHttpResponse()), TradeJson.class);
		
		firstTradeJson.setName("Scott Summers");
		RequestResponseHolder put = SnippetUtil.buildPutRequestResponse(BASE_URL + firstTradeJson.getTradeId(), firstTradeJson);
		String putSnippet = TemplateUtil.buildSnippet(put.getHttpRequest(), put.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_PUT_SNIPPET, putSnippet);
		
		TradeJson postResponse = JsonUtil.fromString(RestUtil.buildResponseBodyString(post.getHttpResponse()), TradeJson.class);
		RequestResponseHolder get = SnippetUtil.buildGetRequestResponse(BASE_URL + postResponse.getTradeId());
		String getSnippet = TemplateUtil.buildSnippet(get.getHttpRequest(), get.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_GET_SNIPPET, getSnippet);

		RequestResponseHolder search = SnippetUtil.buildGetRequestResponse(BASE_URL + "?name=Scott%20Summers&_pageNumber=1&_pageSize=2");
		String searchSnippet = TemplateUtil.buildSnippet(search.getHttpRequest(), search.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_SEARCH_SNIPPET, searchSnippet);

		RequestResponseHolder getAll = SnippetUtil.buildGetRequestResponse(BASE_URL);
		String getAllSnippet = TemplateUtil.buildSnippet(getAll.getHttpRequest(), getAll.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_GET_ALL_SNIPPET, getAllSnippet);
		
		RequestResponseHolder del = SnippetUtil.buildDeleteRequestResponse(BASE_URL + postResponse.getTradeId());
		String delSnippet = TemplateUtil.buildSnippet(del.getHttpRequest(), del.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_DELETE_SNIPPET, delSnippet);
		
		return template;
	}

	@Override
	public String getDocLocation() {
		return "doc/rest/trades.md";
	}

}
