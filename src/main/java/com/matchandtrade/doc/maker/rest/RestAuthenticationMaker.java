package com.matchandtrade.doc.maker.rest;

import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.matchandtrade.doc.maker.OutputMaker;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import static org.hamcrest.Matchers.*;


public class RestAuthenticationMaker extends OutputMaker {
	
	private static final String AUTHENTICATIONS_SNIPPET = "AUTHENTICATIONS_SNIPPET";

	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		SnippetFactory snippetFactory = new SnippetFactory(MatchAndTradeRestUtil.getLastAuthorizationHeader());

		// AUTHENTICATIONS_SNIPPET
		Snippet snippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.authenticationsUrl() + "/");
		snippet.getResponse().then().statusCode(200).and().body("", hasKey("userId"));
		return TemplateUtil.replacePlaceholder(template, AUTHENTICATIONS_SNIPPET, snippet.asHtml());
	}

	@Override
	public String getDocLocation() {
		return "authentications.html";
	}

}
