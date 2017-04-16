package com.github.rafasantos.matchandtrade.doc.maker.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import com.github.rafasantos.matchandtrade.doc.executable.PropertiesProvider;
import com.github.rafasantos.matchandtrade.doc.maker.OutputMaker;
import com.github.rafasantos.matchandtrade.doc.util.AssertUtil;
import com.github.rafasantos.matchandtrade.doc.util.JsonUtil;
import com.github.rafasantos.matchandtrade.doc.util.RequestResponseHolder;
import com.github.rafasantos.matchandtrade.doc.util.TemplateUtil;
import com.github.rafasantos.matchandtrade.exception.DocMakerException;
import com.matchandtrade.rest.v1.json.TradeJson;


public class RestTradesMaker implements OutputMaker {
	
	public static final String TRADES_POST_SNIPPET = "TRADES_POST_SNIPPET";
	private static final String TRADES_GET_SNIPPET = "TRADES_GET_SNIPPET";
	private static final String TRADES_SEARCH_SNIPPET = "TRADES_SEARCH_SNIPPET";
	private static final String TRADES_GET_ALL_SNIPPET = "TRADES_GET_ALL_SNIPPET";
	
	public RequestResponseHolder buildPostRequestResponse(TradeJson tradeJson) {
		HttpClient httpClient = HttpClients.createDefault();
		HttpPost httpRequest = new HttpPost(PropertiesProvider.getServerUrl() + "/rest/v1/trades/");
		httpRequest.addHeader(RestUtil.getAuthenticationHeader());
		httpRequest.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		
		StringEntity requestBody = new StringEntity(JsonUtil.toJson(tradeJson), StandardCharsets.UTF_8);
		httpRequest.setEntity(requestBody);

		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(httpRequest);
		} catch (IOException e) {
			throw new DocMakerException(this, e);
		}
		// Assert if status is 200
		AssertUtil.areEqual(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
		
		return new RequestResponseHolder(httpRequest, httpResponse);
	}
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());

		TradeJson firstTradeJson = new TradeJson();
		firstTradeJson.setName("First testing trade");
		RequestResponseHolder post = buildPostRequestResponse(firstTradeJson);
		String postSnippet = TemplateUtil.buildSnippet(post.getHttpRequest(), post.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_POST_SNIPPET, postSnippet);
		
		RequestResponseHolder get = GetSnippetMaker.buildGetRequestResponse("/rest/v1/trades/1");
		String getSnippet = TemplateUtil.buildSnippet(get.getHttpRequest(), get.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_GET_SNIPPET, getSnippet);

		RequestResponseHolder search = GetSnippetMaker.buildGetRequestResponse("/rest/v1/trades?name=First%20testing%20trade");
		String searchSnippet = TemplateUtil.buildSnippet(search.getHttpRequest(), search.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_SEARCH_SNIPPET, searchSnippet);
		
		// Make as second TradeJson so the getAll can display two results
		TradeJson secondTradeJson = new TradeJson();
		secondTradeJson.setName("Second testing trade");
		buildPostRequestResponse(secondTradeJson);
		RequestResponseHolder getAll = GetSnippetMaker.buildGetRequestResponse("/rest/v1/trades/");
		String getAllSnippet = TemplateUtil.buildSnippet(getAll.getHttpRequest(), getAll.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_GET_ALL_SNIPPET, getAllSnippet);
		
		return template;
	}

	@Override
	public String getDocLocation() {
		return "doc/rest/trades.md";
	}

}
