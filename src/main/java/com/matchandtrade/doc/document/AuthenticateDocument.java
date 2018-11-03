package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import io.restassured.RestAssured;

import static org.hamcrest.Matchers.nullValue;

public class AuthenticateDocument implements Document {

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
		SpecificationParser authenticateParser = AuthenticateDocument.buildGetParser();
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATE_PLACEHOLDER, authenticateParser.asHtmlSnippet());

		// AUTHENTICATE_INFO
		SpecificationParser authenticationInfoParser = buildGetInfoParser(authenticateParser.getResponse().getHeader("Set-Cookie"));
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATE_INFO, authenticationInfoParser.asHtmlSnippet());

		// SIGN_OUT_PLACEHOLDER
		SpecificationParser signOffParser = parseSingOut();
		template = TemplateUtil.replacePlaceholder(template, SIGN_OUT_PLACEHOLDER, signOffParser.asHtmlSnippet());

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
	private SpecificationParser buildGetInfoParser(String authenticateCookie) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.header("cookie", authenticateCookie)
			.filter(filter)
			.get(MatchAndTradeRestUtil.authenticateInfoUrl());
		parser.getResponse().then().statusCode(200);
		return parser;
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
