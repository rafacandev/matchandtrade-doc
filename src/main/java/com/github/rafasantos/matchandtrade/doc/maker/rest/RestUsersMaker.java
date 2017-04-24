package com.github.rafasantos.matchandtrade.doc.maker.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
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
import com.matchandtrade.rest.v1.json.UserJson;


public class RestUsersMaker implements OutputMaker {
	
	private static final String USERS_GET_SNIPPET = "USERS_GET_SNIPPET";
	private static final String USERS_PUT_SNIPPET = "USERS_PUT_SNIPPET";
	
	private RequestResponseHolder buildPutRequestResponse() {
		HttpClient httpClient = HttpClients.createDefault();
		HttpPut httpRequest = new HttpPut(PropertiesProvider.getServerUrl() + "/rest/v1/users/" + RestUtil.getAuthenticatedUser().getUserId());
		httpRequest.addHeader(RestUtil.getAuthenticationHeader());
		httpRequest.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		
		UserJson requestJson = new UserJson();
		requestJson.setEmail(RestUtil.getAuthenticatedUser().getEmail());
		requestJson.setName("User name for PUT method");
		
		String requestBody = JsonUtil.toJson(requestJson);
		
		try {
			httpRequest.setEntity(new StringEntity(requestBody));
		} catch (UnsupportedEncodingException e) {
			throw new DocMakerException(this, e);
		}
		
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(httpRequest);
		} catch (IOException e) {
			throw new DocMakerException(this, e);
		}
		AssertUtil.isEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
		
		return new RequestResponseHolder(httpRequest, httpResponse);
	}


	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());

		RequestResponseHolder get = GetSnippetMaker.buildGetRequestResponse("/rest/v1/users/" + RestUtil.getAuthenticatedUser().getUserId());
		String getSnippet = TemplateUtil.buildSnippet(get.getHttpRequest(), get.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, USERS_GET_SNIPPET, getSnippet);

		RequestResponseHolder put = buildPutRequestResponse();
		String putSnippet = TemplateUtil.buildSnippet(put.getHttpRequest(), put.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, USERS_PUT_SNIPPET, putSnippet);
		
		return template;
	}

	@Override
	public String getDocLocation() {
		return "doc/rest/users.md";
	}

}
