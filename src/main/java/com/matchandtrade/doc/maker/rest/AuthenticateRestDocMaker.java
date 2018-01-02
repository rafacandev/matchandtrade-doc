package com.matchandtrade.doc.maker.rest;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.*;

import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;

public class AuthenticateRestDocMaker implements RestDocMaker {

	private static final String AUTHENTICATE_PLACEHOLDER = "AUTHENTICATE_PLACEHOLDER";
	private static final String AUTHENTICATE_INFO = "AUTHENTICATE_INFO";
	private static final String SIGN_OUT_PLACEHOLDER = "SIGN_OUT_PLACEHOLDER";

	@Override
	public String contentFilePath() {
		return "authenticate.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());
		SnippetFactory snippetFactory = new SnippetFactory();
		
		// AUTHENTICATE_PLACEHOLDER
		Snippet authenticateSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.authenticateUrl());
		// Asserts that statusCode = 200 and header "Authorization" exists
		authenticateSnippet.getResponse().then().statusCode(200).and().header("Authorization", notNullValue());
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATE_PLACEHOLDER, authenticateSnippet.asHtml());
		String cookie = authenticateSnippet.getResponse().getHeader("Set-Cookie");

		// AUTHENTICATE_INFO
		RequestSpecification requestSpecification = new RequestSpecBuilder().addHeader("Cookie", cookie).build();
		Snippet authenticateInfoSnippet = SnippetFactory.makeSnippet(Method.GET, requestSpecification, MatchAndTradeRestUtil.authenticateInfoUrl());
		// Asserts that statusCode = 200 and header "AuthorizationHeader" exists in the body
		authenticateInfoSnippet.getResponse().then().statusCode(200).and().body("authenticationHeader", notNullValue());
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATE_INFO, authenticateInfoSnippet.asHtml());

		// SIGN_OUT_PLACEHOLDER
		Snippet signOffSnippet = SnippetFactory.makeSnippet(Method.GET, requestSpecification, MatchAndTradeRestUtil.signOffUrl());
		// Asserts that statusCode = 205 and header "Authorization" does not exists
		signOffSnippet.getResponse().then().statusCode(205).and().header("Authorization", nullValue());
		template = TemplateUtil.replacePlaceholder(template, SIGN_OUT_PLACEHOLDER, signOffSnippet.asHtml());

		return TemplateUtil.appendHeaderAndFooter(template);
	}

}
