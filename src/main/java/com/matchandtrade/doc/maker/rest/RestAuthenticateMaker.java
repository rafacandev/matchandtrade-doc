package com.matchandtrade.doc.maker.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpStatus;

import com.matchandtrade.doc.maker.OutputMaker;
import com.matchandtrade.doc.util.RequestResponseHolder;
import com.matchandtrade.doc.util.RequestResponseUtil;
import com.matchandtrade.doc.util.RestUtil;
import com.matchandtrade.doc.util.TemplateUtil;

public class RestAuthenticateMaker extends OutputMaker {

	public static final String AUTHENTICATE_SNIPPET = "AUTHENTICATE_SNIPPET";
	public static final String SIGN_OUT_SNIPPET = "SIGN_OUT_SNIPPET";

	@Override
	public String buildDocContent() {
		// AUTHENTICATE_SNIPPET
		String template = TemplateUtil.buildTemplate(getDocLocation());
		RestUtil.setAuthenticationHeader(null);
		RequestResponseHolder authenticateRequesResponse = RequestResponseUtil.buildAuthenticateRequestResponse();
		String authenticateSnippet = TemplateUtil.buildSnippet(authenticateRequesResponse.getHttpRequest(), authenticateRequesResponse.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATE_SNIPPET, authenticateSnippet);
		
		// SIGN_OUT_SNIPPET
		List<Header> signOutHeaders = new ArrayList<>();
		signOutHeaders.add(authenticateRequesResponse.getAuthorizationHeader());
		RequestResponseHolder signOut = RequestResponseUtil.buildGetRequestResponse("/authenticate/sign-out", signOutHeaders, HttpStatus.SC_RESET_CONTENT);
		String signOutSnippet = TemplateUtil.buildSnippet(signOut.getHttpRequest(), signOut.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, SIGN_OUT_SNIPPET, signOutSnippet);
		return template;
	}

	@Override
	public String getDocLocation() {
		return "authenticate.html";
	}
}
