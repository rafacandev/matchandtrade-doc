package com.github.rafasantos.matchandtrade.doc.maker.rest;

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
	public static final String SIGN_OFF_SNIPPET = "SIGN_OFF_SNIPPET";

	/**
	 * Build a RequestResponseHolder for GET /authenticate.
	 * Making this method public as it is also used on {@code RestUtil} and {@code RestGuideMaker}
	 * 
	 * @return RequestResponseHolder for authenticate
	 */
	public RequestResponseHolder buildAuthenticateRequestResponse() {
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

	private String buildSingOffSnippet(HttpResponse authenticatedResponse) {
		HttpClient httpClient = HttpClients.createDefault();
		HttpGet httpRequest = new HttpGet(PropertiesProvider.getServerUrl() + "/authenticate/sign-out");
		
		// Add the previous Authorization header to the request
		Header[] authenticatedResponseHeaders = authenticatedResponse.getAllHeaders();
		for (int i=0; i < authenticatedResponseHeaders.length; i++) {
			if (authenticatedResponseHeaders[i].getName().equals("Authorization")) {
				httpRequest.addHeader(authenticatedResponseHeaders[i]);
			}
		}

		HttpResponse httpResponse;
		try {
			// Execute the request
			httpResponse = httpClient.execute(httpRequest);
		} catch (Exception e) {
			throw new DocMakerException(this, e);
		}
		
		// Assert if status is 200
		AssertUtil.areEqual(HttpStatus.SC_RESET_CONTENT, httpResponse.getStatusLine().getStatusCode());
		
		// Assert if contains Authorization header
		Set<String> headers = new HashSet<>();
		for (Header h : httpResponse.getAllHeaders()) {
			headers.add(h.getName());
		}
		AssertUtil.isTrue(!headers.toString().contains("Authorization"));
		
		return TemplateUtil.buildSnippet(httpRequest, httpResponse); 
	}


	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		RequestResponseHolder authenticateRequesResponse = buildAuthenticateRequestResponse();
		String authenticateSnippet = TemplateUtil.buildSnippet(authenticateRequesResponse.getHttpRequest(), authenticateRequesResponse.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATE_SNIPPET, authenticateSnippet);
		
		String signOffSnippet = buildSingOffSnippet(authenticateRequesResponse.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, SIGN_OFF_SNIPPET, signOffSnippet);
		return template;
	}

	@Override
	public String getDocLocation() {
		return "doc/rest/authenticate.md";
	}
}
