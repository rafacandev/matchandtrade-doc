package com.matchandtrade.doc.maker.rest;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.matchandtrade.doc.maker.TemplateHelper;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import io.restassured.RestAssured;

import static org.hamcrest.Matchers.nullValue;

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

		// AUTHENTICATE_PLACEHOLDER
		SpecificationParser authenticateParser = parseAuthenticate();
		template = TemplateHelper.replacePlaceholder(template, AUTHENTICATE_PLACEHOLDER, authenticateParser.asHtmlSnippet());

		// AUTHENTICATE_INFO
		SpecificationParser authenticationInfoParser = parseAuthenticateInfo(authenticateParser.getResponse().getHeader("Set-Cookie"));
		template = TemplateHelper.replacePlaceholder(template, AUTHENTICATE_INFO, authenticationInfoParser.asHtmlSnippet());

		// SIGN_OUT_PLACEHOLDER
		SpecificationParser signOffParser = parseSingOut();
		template = TemplateHelper.replacePlaceholder(template, SIGN_OUT_PLACEHOLDER, signOffParser.asHtmlSnippet());

		return TemplateUtil.appendHeaderAndFooter(template);
	}

	private SpecificationParser parseSingOut() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
			.get(MatchAndTradeRestUtil.signOffUrl());
		parser.getResponse().then().statusCode(205).header("Authorization", nullValue());
		return parser;
	}

	/**
	 * Need to keep the same cookie between "authenticate" and "authenticate-info"
	 * @param authenticateCookie cookie value from "authenticate" endpoint
	 * @return
	 */
	private SpecificationParser parseAuthenticateInfo(String authenticateCookie) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.header("cookie", authenticateCookie)
			.filter(filter)
			.get(MatchAndTradeRestUtil.authenticateInfoUrl());
		parser.getResponse().then().statusCode(200);
		return parser;
	}

	private SpecificationParser parseAuthenticate() {
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
