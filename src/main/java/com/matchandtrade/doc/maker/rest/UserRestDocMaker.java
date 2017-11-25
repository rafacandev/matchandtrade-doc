
package com.matchandtrade.doc.maker.rest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.rest.v1.json.UserJson;

import io.restassured.http.ContentType;
import io.restassured.http.Method;


public class UserRestDocMaker implements RestDocMaker {
	
	private static final String USERS_GET_SNIPPET = "USERS_GET_SNIPPET";
	private static final String USERS_PUT_SNIPPET = "USERS_PUT_SNIPPET";
	@Override
	public String contentFilePath() {
		return "users.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());
		SnippetFactory snippetFactory = new SnippetFactory(ContentType.JSON, MatchAndTradeRestUtil.getLastAuthorizationHeader());
		
		// USERS_PUT_SNIPPET
		UserJson userJson = new UserJson();
		userJson.setEmail(MatchAndTradeRestUtil.getLastAuthenticatedUser().getEmail());
		userJson.setName("Scott Summers");
		Snippet putSnippet = snippetFactory.makeSnippet(Method.PUT, userJson, MatchAndTradeRestUtil.usersUrl() + "/" + MatchAndTradeRestUtil.getLastAuthenticatedUserId());
		putSnippet.getResponse().then().statusCode(200).and().body("name", equalTo(userJson.getName()));
		template = TemplateUtil.replacePlaceholder(template, USERS_PUT_SNIPPET, putSnippet.asHtml());

		// USERS_GET_SNIPPET
		Snippet getSnippett = snippetFactory.makeSnippet(MatchAndTradeRestUtil.usersUrl() + "/" + MatchAndTradeRestUtil.getLastAuthenticatedUserId());
		getSnippett.getResponse().then().statusCode(200).and().body("", hasKey("userId"));
		template = TemplateUtil.replacePlaceholder(template, USERS_GET_SNIPPET, getSnippett.asHtml());
		
		return TemplateUtil.appendHeaderAndFooter(template);
	}

}
