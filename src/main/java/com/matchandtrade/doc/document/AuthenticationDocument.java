package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import io.restassured.RestAssured;

import static org.hamcrest.Matchers.hasKey;


public class AuthenticationDocument implements Document {
	
	private static final String AUTHENTICATIONS_PLACEHOLDER = "AUTHENTICATIONS_PLACEHOLDER";

	@Override
	public String contentFilePath() {
		return "authentications.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());

		// AUTHENTICATIONS_PLACEHOLDER
		SpecificationParser parser = buildGetParser();
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATIONS_PLACEHOLDER, parser.asHtmlSnippet());

		return TemplateUtil.appendHeaderAndFooter(template);
	}

	public static SpecificationParser buildGetParser() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
			.get(MatchAndTradeRestUtil.authenticationsUrl() + "/");
		parser.getResponse().then().statusCode(200).and().body("", hasKey("userId"));
		return parser;
	}

}
