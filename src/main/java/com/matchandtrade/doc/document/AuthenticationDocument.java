package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.util.MatchAndTradeClient;
import com.matchandtrade.doc.util.TemplateUtil;


public class AuthenticationDocument implements Document {
	
	private static final String AUTHENTICATIONS_PLACEHOLDER = "AUTHENTICATIONS_PLACEHOLDER";

	private MatchAndTradeClient clientApi;
	private String template;

	public AuthenticationDocument() {
		clientApi = new MatchAndTradeClient();
		template = TemplateUtil.buildTemplate(contentFilePath());
	}

	@Override
	public String content() {
		// AUTHENTICATIONS_PLACEHOLDER
		SpecificationParser parser = clientApi.findAuthentications();
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATIONS_PLACEHOLDER, parser.asHtmlSnippet());

		return TemplateUtil.appendHeaderAndFooter(template);
	}

	@Override
	public String contentFilePath() {
		return "authentications.html";
	}

}
