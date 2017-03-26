package com.github.rafasantos.matchandtrade.doc.maker.rest;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.github.rafasantos.matchandtrade.doc.executable.PropertiesProvider;
import com.github.rafasantos.matchandtrade.doc.maker.OutputMaker;
import com.github.rafasantos.matchandtrade.doc.util.AssertUtil;
import com.github.rafasantos.matchandtrade.doc.util.RequestResponseHolder;
import com.github.rafasantos.matchandtrade.doc.util.TemplateUtil;
import com.github.rafasantos.matchandtrade.exception.DocMakerException;

public class RestAuthenticateMaker implements OutputMaker {

	public static final String AUTHENTICATE_POSITIVE_SNIPPET = "AUTHENTICATE_POSITIVE_SNIPPET";

	public RequestResponseHolder testPositive() {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpRequest = new HttpGet(PropertiesProvider.getServerUrl() + "/authenticate");
		try {
			// Execute the request
			CloseableHttpResponse httpResponse = httpClient.execute(httpRequest);
			
			// Assert if status is 200
			AssertUtil.areEqual(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
			
			// Assert if contains Authorization header
			Set<String> headers = new HashSet<>();
			for (Header h : httpResponse.getAllHeaders()) {
				headers.add(h.getName());
			}
			AssertUtil.isTrue(headers.toString().contains("Authorization"));
			
			return new RequestResponseHolder(httpRequest, httpResponse);
		} catch (Exception e) {
			throw new DocMakerException(this, e);
		}
	}

	public String buildAuthenticatePositiveSnippet(HttpGet httpRequest) {
		return TemplateUtil.buildRequestSnippet(httpRequest);
	}

	private String buildDocContent(HttpGet httpRequest) throws IOException {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		String snippet = buildAuthenticatePositiveSnippet(httpRequest);
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATE_POSITIVE_SNIPPET, snippet);
		return template;
	}

	@Override
	public String obtainDocContent() {
		try {
			RequestResponseHolder requestResponseBag = testPositive();
			return buildDocContent(requestResponseBag.getHttpRequest());
		} catch (IOException e) {
			throw new DocMakerException(this, e);
		}
	}

	@Override
	public String getDocLocation() {
		return "doc/rest/authenticate.md";
	}
}
