package com.github.rafasantos.matchandtrade.doc.maker.rest;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.Header;
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

public class RestAuthenticateMaker implements OutputMaker {

	public static final String AUTHENTICATE_SNIPPET = "AUTHENTICATE_SNIPPET";

	public RequestResponseHolder testPositive() {
		HttpClient httpClient = HttpClients.createDefault();
		HttpGet httpRequest = new HttpGet(PropertiesProvider.getServerUrl() + "/authenticate");
		HttpResponse httpResponse;
		try {
			// Execute the request
			httpResponse = httpClient.execute(httpRequest);
		} catch (Exception e) {
			throw new DocMakerException(this, e);
		}
			
		// Assert if status is 200
		AssertUtil.areEqual(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
		
		// Assert if contains Authorization header
		Set<String> headers = new HashSet<>();
		for (Header h : httpResponse.getAllHeaders()) {
			headers.add(h.getName());
		}
		AssertUtil.isTrue(headers.toString().contains("Authorization"));
		
		return new RequestResponseHolder(httpRequest, httpResponse);
	}

	public String buildPositiveSnippet(HttpGet httpRequest, HttpResponse httpResponse) {
		return TemplateUtil.buildRequestSnippet(httpRequest, httpResponse);
	}

	private String buildDocContent(HttpGet httpRequest, HttpResponse httpResponse) throws IOException {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		String snippet = buildPositiveSnippet(httpRequest, httpResponse);
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATE_SNIPPET, snippet);
		return template;
	}

	@Override
	public String obtainDocContent() {
		try {
			RequestResponseHolder requestResponseHolder = testPositive();
			return buildDocContent(requestResponseHolder.getHttpRequest(), requestResponseHolder.getHttpResponse());
		} catch (IOException e) {
			throw new DocMakerException(this, e);
		}
	}

	@Override
	public String getDocLocation() {
		return "doc/rest/authenticate.md";
	}
}
