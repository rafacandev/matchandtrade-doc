package com.github.rafasantos.matchandtrade.doc.maker.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
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
import com.matchandtrade.config.AuthenticationProperties;

public class RestAuthenticateMaker implements OutputMaker {

	public static final String AUTHENTICATE_POSITIVE_PLACEHOLDER = "AUTHENTICATE_POSITIVE_PLACEHOLDER";

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

	public String buildAuthenticatePositiveSnippet(HttpGet httpRequest, HttpResponse httpResponse) {
		StringBuilder result = new StringBuilder();
		result.append("--- REQUEST\n");
		result.append(httpRequest.getMethod() + " " + httpRequest.getURI());
		result.append("\n\n");
		result.append("--- RESPONSE\n");
		result.append(httpResponse.getStatusLine().getProtocolVersion() + " ");
		result.append(httpResponse.getStatusLine().getStatusCode() + " ");
		result.append(Response.Status.fromStatusCode(httpResponse.getStatusLine().getStatusCode()).getReasonPhrase());
		Header[] authoHeaders = httpResponse.getHeaders(AuthenticationProperties.OAuth.AUTHORIZATION_HEADER.toString());
		result.append("\nHeaders: ");
		for (int i = 0; i < authoHeaders.length; i++) {
			result.append("[" + authoHeaders[i].getName() + ": ");
			result.append(authoHeaders[i].getValue() + "]");
		}
		String responseBody;
		try {
			responseBody = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new DocMakerException(this, e);
		}
		result.append("\n\n" + responseBody);
		return result.toString();
	}

	private String buildDocOutput(HttpGet httpRequest, HttpResponse httpResponse) throws IOException {
		String template = TemplateUtil.buildTemplate("doc/rest/resources/authenticate.md");
		String snippet = buildAuthenticatePositiveSnippet(httpRequest, httpResponse);
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATE_POSITIVE_PLACEHOLDER, snippet);
		return template;
	}

	@Override
	public String obtainDocContent() {
		try {
			RequestResponseHolder requestResponseBag = testPositive();
			return buildDocOutput(requestResponseBag.getHttpRequest(), requestResponseBag.getHttpResponse());
		} catch (IOException e) {
			throw new DocMakerException(this, e);
		}
	}

	@Override
	public String obtainDocLocation() {
		return "rest/resources/authenticate.md";
	}
}
