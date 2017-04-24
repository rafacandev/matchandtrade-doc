package com.github.rafasantos.matchandtrade.doc.maker.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.MediaType;

import org.apache.http.Header;
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
import com.github.rafasantos.matchandtrade.doc.util.GetSnippetMaker;
import com.github.rafasantos.matchandtrade.doc.util.JsonUtil;
import com.github.rafasantos.matchandtrade.doc.util.RequestResponseHolder;
import com.github.rafasantos.matchandtrade.doc.util.TemplateUtil;
import com.github.rafasantos.matchandtrade.exception.DocMakerException;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;


public class RestTradeMembershipsMaker implements OutputMaker {
	
	public static final String TRADES_MEMBERSHIP_POST_SNIPPET = "TRADES_MEMBERSHIP_POST_SNIPPET";
	public static final String TRADES_MEMBERSHIP_GET_SNIPPET = "TRADES_MEMBERSHIP_GET_SNIPPET";
	public static final String TRADES_MEMBERSHIP_GET_ALL_SNIPPET = "TRADES_MEMBERSHIP_GET_ALL_SNIPPET";
	public static final String TRADES_MEMBERSHIP_SEARCH_SNIPPET = "TRADES_MEMBERSHIP_SEARCH_SNIPPET";
	
	public RequestResponseHolder buildPostRequestResponse(TradeMembershipJson json) {
		HttpClient httpClient = HttpClients.createDefault();
		HttpPost httpRequest = new HttpPost(PropertiesProvider.getServerUrl() + "/rest/v1/trade-memberships/");
		httpRequest.addHeader(RestUtil.getAuthenticationHeader());
		httpRequest.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		
		StringEntity requestBody = new StringEntity(JsonUtil.toJson(json), StandardCharsets.UTF_8);
		httpRequest.setEntity(requestBody);

		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(httpRequest);
		} catch (IOException e) {
			throw new DocMakerException(this, e);
		}

		// Assert if status is 200
		AssertUtil.isEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
		
		return new RequestResponseHolder(httpRequest, httpResponse);
	}
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		
		// Need to authenticate as a new user because because the current user is already the owner of the previous trade
		RestAuthenticateMaker authenticate = new RestAuthenticateMaker();
		RequestResponseHolder requestResponseAuth = authenticate.buildAuthenticateRequestResponse();
		Header authorizationHeader = requestResponseAuth.getHttpResponse().getHeaders("Authorization")[0];
		RestUtil.setAuthenticationHeader(authorizationHeader);
		// Become member of one of the previous trades
		TradeMembershipJson postJson = new TradeMembershipJson();
		postJson.setUserId(RestUtil.getAuthenticatedUser().getUserId());
		postJson.setTradeId(1);
		RequestResponseHolder post = buildPostRequestResponse(postJson);
		String postSnippet = TemplateUtil.buildSnippet(post.getHttpRequest(), post.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_POST_SNIPPET, postSnippet);
		
		RequestResponseHolder get = GetSnippetMaker.buildGetRequestResponse("/rest/v1/trade-memberships/3");
		String getSnippet = TemplateUtil.buildSnippet(get.getHttpRequest(), get.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_GET_SNIPPET, getSnippet);

		RequestResponseHolder getAll = GetSnippetMaker.buildGetRequestResponse("/rest/v1/trade-memberships/");
		String getAllSnippet = TemplateUtil.buildSnippet(getAll.getHttpRequest(), getAll.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_GET_ALL_SNIPPET, getAllSnippet);

		RequestResponseHolder search = GetSnippetMaker.buildGetRequestResponse("/rest/v1/trade-memberships?userId="+RestUtil.getAuthenticatedUser().getUserId()+"&tradeId=1&_pageNumber=1&_pageSize=10");
		String searchSnippet = TemplateUtil.buildSnippet(search.getHttpRequest(), search.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_SEARCH_SNIPPET, searchSnippet);
		
		return template;
	}

	@Override
	public String getDocLocation() {
		return "doc/rest/trade-memberships.md";
	}

}
