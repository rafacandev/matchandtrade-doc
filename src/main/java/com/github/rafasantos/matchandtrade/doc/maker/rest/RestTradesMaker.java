package com.github.rafasantos.matchandtrade.doc.maker.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
	
	private static final String TRADES_POST_SNIPPET = "TRADES_POST_SNIPPET";
	private static final String TRADES_GET_SNIPPET = "TRADES_GET_SNIPPET";
	
	private RequestResponseHolder buildPostRequestResponse() {
		HttpClient httpClient = HttpClients.createDefault();
		HttpPost httpRequest = new HttpPost(PropertiesProvider.getServerUrl() + "/rest/v1/trades/");
		httpRequest.addHeader(RestUtil.getAuthenticationHeader());
		httpRequest.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		
		TradeJson tradeJson = new TradeJson();
		tradeJson.setName("Testing Trade Name");
		
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
	
	private RequestResponseHolder buildGetRequestResponse() {
		HttpClient httpClient = HttpClients.createDefault();
		HttpGet httpRequest = new HttpGet(PropertiesProvider.getServerUrl() + "/rest/v1/trades/1");
		httpRequest.addHeader(RestUtil.getAuthenticationHeader());
		httpRequest.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
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
	public String obtainDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());

		RequestResponseHolder post = buildPostRequestResponse();
		String postSnippet = TemplateUtil.buildSnippet(post.getHttpRequest(), post.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_POST_SNIPPET, postSnippet);
		
		RequestResponseHolder get = buildGetRequestResponse();
		String getSnippet = TemplateUtil.buildSnippet(get.getHttpRequest(), get.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_GET_SNIPPET, getSnippet);
		
		return template;
	}

	@Override
	public String getDocLocation() {
		return "doc/rest/trades.md";
	}

}
