package com.matchandtrade.doc.maker.rest;

import static org.hamcrest.Matchers.hasKey;

import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;


public class AuthenticationRestDocMaker implements RestDocMaker {
	
	private static final String AUTHENTICATIONS_PLACEHOLDER = "AUTHENTICATIONS_PLACEHOLDER";

	@Override
	public String contentFilePath() {
		return "authentications.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());
		SnippetFactory snippetFactory = new SnippetFactory(MatchAndTradeRestUtil.getLastAuthorizationHeader());

		// AUTHENTICATIONS_PLACEHOLDER
		Snippet snippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.authenticationsUrl() + "/");
		snippet.getResponse().then().statusCode(200).and().body("", hasKey("userId"));

		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATIONS_PLACEHOLDER, snippet.asHtml());
		return TemplateUtil.appendHeaderAndFooter(template);
	}

}
