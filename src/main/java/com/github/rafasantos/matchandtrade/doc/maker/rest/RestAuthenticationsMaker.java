package com.github.rafasantos.matchandtrade.doc.maker.rest;

import java.io.IOException;

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


public class RestAuthenticationsMaker implements OutputMaker {
	
	public static final String AUTHENTICATIONS_SNIPPET = "AUTHENTICATIONS_SNIPPET";
	
	public RequestResponseHolder buildGetAuthenticationsRequestResponse() {
		HttpClient httpClient = HttpClients.createDefault();
		HttpGet httpRequest = new HttpGet(PropertiesProvider.getServerUrl() + "/rest/v1/authentications/");
		httpRequest.addHeader(RestUtil.getAuthenticationHeader());
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
		RequestResponseHolder requestResponse = buildGetAuthenticationsRequestResponse();
		String template = TemplateUtil.buildTemplate(getDocLocation());
		String snippet = TemplateUtil.buildSnippet(requestResponse.getHttpRequest(), requestResponse.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATIONS_SNIPPET, snippet);
		return template;
	}

	@Override
	public String getDocLocation() {
		return "doc/rest/authentications.md";
	}

}
