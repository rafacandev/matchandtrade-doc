package com.github.rafasantos.matchandtrade.doc.maker.rest;

import java.io.IOException;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import com.github.rafasantos.matchandtrade.doc.executable.PropertiesProvider;
import com.github.rafasantos.matchandtrade.doc.maker.OutputMaker;
import com.github.rafasantos.matchandtrade.doc.util.AssertUtil;
import com.github.rafasantos.matchandtrade.doc.util.RequestResponseHolder;
import com.github.rafasantos.matchandtrade.doc.util.TemplateUtil;
import com.github.rafasantos.matchandtrade.exception.DocMakerException;


public class RestUsersMaker implements OutputMaker {
	
	private static final String USERS_GET_SNIPPET = "USERS_GET_SNIPPET";
	
	public RequestResponseHolder buildGetRequestResponse() {
		HttpClient httpClient = HttpClients.createDefault();
		HttpGet httpRequest = new HttpGet(PropertiesProvider.getServerUrl() + "/rest/v1/users/1");
		httpRequest.addHeader(RestUtil.getAuthenticationHeader());
		httpRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
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

		RequestResponseHolder get = buildGetRequestResponse();
		String getSnippet = TemplateUtil.buildSnippet(get.getHttpRequest(), get.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, USERS_GET_SNIPPET, getSnippet);
		
		return template;
	}

	@Override
	public String getDocLocation() {
		return "doc/rest/users.md";
	}

}
