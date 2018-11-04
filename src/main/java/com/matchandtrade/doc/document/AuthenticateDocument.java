package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.util.MatchAndTradeClient;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import io.restassured.RestAssured;

public class AuthenticateDocument implements Document {

	private static final String AUTHENTICATE_PLACEHOLDER = "AUTHENTICATE_PLACEHOLDER";
	private static final String AUTHENTICATE_INFO = "AUTHENTICATE_INFO";
	private static final String SIGN_OUT_PLACEHOLDER = "SIGN_OUT_PLACEHOLDER";

	private MatchAndTradeClient clientApi;
	private String template;

	public AuthenticateDocument() {
		clientApi = new MatchAndTradeClient();
		template = TemplateUtil.buildTemplate(contentFilePath());
	}

	@Override
	public String content() {
		// AUTHENTICATE_PLACEHOLDER
		SpecificationParser authenticateParser = MatchAndTradeClient.authenticate();
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATE_PLACEHOLDER, authenticateParser.asHtmlSnippet());
		String cookie = authenticateParser.getResponse().getHeader("Set-Cookie");

		// AUTHENTICATE_INFO
		SpecificationParser authenticationInfoParser = clientApi.findAuthenticationInfo(cookie);
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATE_INFO, authenticationInfoParser.asHtmlSnippet());

		// SIGN_OUT_PLACEHOLDER
		SpecificationParser signOffParser = clientApi.singOff();
		template = TemplateUtil.replacePlaceholder(template, SIGN_OUT_PLACEHOLDER, signOffParser.asHtmlSnippet());

		return TemplateUtil.appendHeaderAndFooter(template);
	}

	@Override
	public String contentFilePath() {
		return "authenticate.html";
	}

	public static SpecificationParser buildGetParser() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.get(MatchAndTradeRestUtil.authenticateUrl());
		// Assert status is redirect
		parser.getResponse().then().statusCode(302);
		return parser;
	}

}
