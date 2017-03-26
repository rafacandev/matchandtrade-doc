package com.github.rafasantos.matchandtrade.doc.maker.rest;

import com.github.rafasantos.matchandtrade.doc.maker.OutputMaker;
import com.github.rafasantos.matchandtrade.doc.util.RequestResponseHolder;
import com.github.rafasantos.matchandtrade.doc.util.TemplateUtil;

public class RestMaker implements OutputMaker {
	
	@Override
	public String obtainDocContent() {
		String template = TemplateUtil.buildTemplate("doc/rest/rest.md");
		RestAuthenticateMaker authenticate = new RestAuthenticateMaker();
		RequestResponseHolder rrHolder = authenticate.testPositive();
		String authenticateSnippet = authenticate.buildAuthenticatePositiveSnippet(rrHolder.getHttpRequest());
		template = TemplateUtil.replacePlaceholder(template, RestAuthenticateMaker.AUTHENTICATE_POSITIVE_SNIPPET, authenticateSnippet);
		return template;
	}
	
	@Override
	public String obtainDocLocation() {
		return "rest/rest.md";
	}
}
