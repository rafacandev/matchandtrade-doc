package com.github.rafasantos.matchandtrade.doc.maker.rest;

import com.github.rafasantos.matchandtrade.doc.maker.OutputMaker;
import com.github.rafasantos.matchandtrade.doc.util.SnippetUtil;
import com.github.rafasantos.matchandtrade.doc.util.RequestResponseHolder;
import com.github.rafasantos.matchandtrade.doc.util.TemplateUtil;


public class RestAuthenticationsMaker implements OutputMaker {
	
	public static final String AUTHENTICATIONS_SNIPPET = "AUTHENTICATIONS_SNIPPET";
	
	public RequestResponseHolder buildGetAuthenticationsRequestResponse() {
		return SnippetUtil.buildGetRequestResponse("/rest/v1/authentications/");
	}

	@Override
	public String buildDocContent() {
		RequestResponseHolder requestResponse = buildGetAuthenticationsRequestResponse();
		String template = TemplateUtil.buildTemplate(getDocLocation());
		String snippet = TemplateUtil.buildSnippet(requestResponse.getHttpRequest(), requestResponse.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATIONS_SNIPPET, snippet);
		return template;
	}

	@Override
	public String getDocLocation() {
		return "doc/rest/authentications.md";
	}

}
