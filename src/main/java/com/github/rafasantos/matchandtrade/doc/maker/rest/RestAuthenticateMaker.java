package com.github.rafasantos.matchandtrade.doc.maker.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import com.github.rafasantos.matchandtrade.doc.maker.OutputMaker;
import com.github.rafasantos.matchandtrade.doc.util.AssertUtil;
import com.github.rafasantos.matchandtrade.doc.util.SnippetUtil;
import com.github.rafasantos.matchandtrade.doc.util.RequestResponseHolder;
import com.github.rafasantos.matchandtrade.doc.util.TemplateUtil;

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
		RequestResponseHolder result = SnippetUtil.buildGetRequestResponse("/authenticate", new ArrayList<Header>(), HttpStatus.SC_OK);

		// Assert if contains Authorization header
		Set<String> headers = new HashSet<>();
		for (Header h : result.getHttpResponse().getAllHeaders()) {
			headers.add(h.getName());
		}
		AssertUtil.isTrue(headers.toString().contains("Authorization"));
		
		return result;
	}

	private String buildSingOffSnippet(HttpResponse authenticatedResponse) {
		// Add the previous Authorization header to the request
		List<Header> authenticatedHeaders = new ArrayList<>();
		Header[] authenticatedResponseHeaders = authenticatedResponse.getAllHeaders();
		for (int i=0; i < authenticatedResponseHeaders.length; i++) {
			if (authenticatedResponseHeaders[i].getName().equals("Authorization")) {
				authenticatedHeaders.add(authenticatedResponseHeaders[i]);
			}
		}

		RequestResponseHolder result = SnippetUtil.buildGetRequestResponse("/authenticate/sign-out", authenticatedHeaders, HttpStatus.SC_RESET_CONTENT);
		
		// Assert if contains Authorization header
		Set<String> headers = new HashSet<>();
		for (Header h : result.getHttpResponse().getAllHeaders()) {
			headers.add(h.getName());
		}
		AssertUtil.isTrue(!headers.toString().contains("Authorization"));
		
		return TemplateUtil.buildSnippet(result.getHttpRequest(), result.getHttpResponse()); 
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
