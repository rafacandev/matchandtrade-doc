package com.github.rafasantos.matchandtrade.doc.maker;

import org.apache.http.Header;

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
		RequestResponseHolder requestResponseAuth = authenticate.buildAuthenticateRequestResponse();
		String authenticateSnippet = TemplateUtil.buildSnippet(requestResponseAuth.getHttpRequest(), requestResponseAuth.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, RestAuthenticateMaker.AUTHENTICATE_SNIPPET, authenticateSnippet);

		// Use the same authorization header for documentation clarity and consistency
		Header authorizationHeader = requestResponseAuth.getHttpResponse().getHeaders("Authorization")[0];
		
		// Assemble authentications snippet		
		RestAuthenticationsMaker authentications = new RestAuthenticationsMaker();
		// Reuse the same Authorization header for better documentation clarity
		RequestResponseHolder requestResponseAuthentications = authentications.buildGetAuthenticationsRequestResponse(authorizationHeader);
		String authenticationsSnippet = TemplateUtil.buildSnippet(requestResponseAuthentications.getHttpRequest(), requestResponseAuthentications.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, RestAuthenticationsMaker.AUTHENTICATIONS_SNIPPET, authenticationsSnippet);

		return template;
	}
	
	@Override
	public String getDocLocation() {
		return "doc/rest-guide.md";
	}
}
