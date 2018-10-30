
package com.matchandtrade.doc.maker.rest;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.maker.DocumentContent;
import com.matchandtrade.doc.maker.TemplateHelper;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.rest.v1.json.UserJson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static org.hamcrest.Matchers.equalTo;


public class UserRestDocMaker implements DocumentContent {
	
	private static final String USERS_GET_PLACEHOLDER = "USERS_GET_PLACEHOLDER";
	private static final String USERS_PUT_PLACEHOLDER = "USERS_PUT_PLACEHOLDER";
	@Override
	public String contentFilePath() {
		return "users.html";
	}

	@Override
	public String content() {
		String template = TemplateHelper.buildTemplate(contentFilePath());

		// USERS_PUT_PLACEHOLDER
		SpecificationParser putUserParser = parsePutUser();
		template = TemplateHelper.replacePlaceholder(template, USERS_PUT_PLACEHOLDER, putUserParser.asHtmlSnippet());

		// USERS_GET_PLACEHOLDER
		SpecificationParser parser = parseGetUser();
		template = TemplateHelper.replacePlaceholder(template, USERS_GET_PLACEHOLDER, parser.asHtmlSnippet());

		return TemplateHelper.appendHeaderAndFooter(template);
	}

	private SpecificationParser parseGetUser() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
			.get(MatchAndTradeRestUtil.usersUrl() + "/" + MatchAndTradeRestUtil.getLastAuthenticatedUserId());
		parser.getResponse().then().statusCode(200);
		return parser;
	}

	private SpecificationParser parsePutUser() {
		UserJson userJson = new UserJson();
		userJson.setEmail(MatchAndTradeRestUtil.getLastAuthenticatedUser().getEmail());
		userJson.setName("Scott Summers");
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
			.contentType(ContentType.JSON)
			.body(userJson)
			.put(MatchAndTradeRestUtil.usersUrl() + "/" + MatchAndTradeRestUtil.getLastAuthenticatedUserId());
		parser.getResponse().then().statusCode(200).and().body("name", equalTo(userJson.getName()));
		return parser;
	}

}
