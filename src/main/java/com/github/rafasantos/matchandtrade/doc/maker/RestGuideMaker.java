package com.github.rafasantos.matchandtrade.doc.maker;

import com.github.rafasantos.matchandtrade.doc.maker.rest.RestAuthenticateMaker;
import com.github.rafasantos.matchandtrade.doc.util.RequestResponseHolder;
import com.github.rafasantos.matchandtrade.doc.util.TemplateUtil;

public class RestGuideMaker implements OutputMaker {
	
	@Override
	public String obtainDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		RestAuthenticateMaker authenticate = new RestAuthenticateMaker();
		RequestResponseHolder rrHolder = authenticate.testPositive();
		String authenticateSnippet = authenticate.buildAuthenticatePositiveSnippet(rrHolder.getHttpRequest());
		template = TemplateUtil.replacePlaceholder(template, RestAuthenticateMaker.AUTHENTICATE_POSITIVE_SNIPPET, authenticateSnippet);
		return template;
	}
	
	@Override
	public String getDocLocation() {
		return "doc/rest-guide.md";
	}
}
