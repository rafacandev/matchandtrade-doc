package com.github.rafasantos.matchandtrade.doc.maker;

import com.github.rafasantos.matchandtrade.doc.maker.rest.RestAuthenticateMaker;
import com.github.rafasantos.matchandtrade.doc.maker.rest.RestAuthenticationsMaker;
import com.github.rafasantos.matchandtrade.doc.util.RequestResponseHolder;
import com.github.rafasantos.matchandtrade.doc.util.TemplateUtil;

public class RestGuideMaker implements OutputMaker {
	
	@Override
	public String obtainDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		
		// Assemble authentication snippet
		RestAuthenticateMaker authenticate = new RestAuthenticateMaker();
		RequestResponseHolder rrHolderAuth = authenticate.testPositive();
		String authenticateSnippet = authenticate.buildPositiveSnippet(rrHolderAuth.getHttpRequest(), rrHolderAuth.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, RestAuthenticateMaker.AUTHENTICATE_SNIPPET, authenticateSnippet);

		// Assemble authentications snippet		
		RestAuthenticationsMaker authentications = new RestAuthenticationsMaker();
		// Reuse the same Authorization header for better documentation clarity
		RequestResponseHolder rrHolderAuthentications = authentications.testPositive(rrHolderAuth.getHttpResponse().getHeaders("Authorization")[0]);
		String authenticationsSnippet = authentications.buildPositiveSnippet(rrHolderAuthentications.getHttpRequest(), rrHolderAuthentications.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, RestAuthenticationsMaker.AUTHENTICATIONS_SNIPPET, authenticationsSnippet);

		return template;
	}
	
	@Override
	public String getDocLocation() {
		return "doc/rest-guide.md";
	}
}
