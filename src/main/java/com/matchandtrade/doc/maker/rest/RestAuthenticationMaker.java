package com.matchandtrade.doc.maker.rest;

import com.matchandtrade.doc.maker.OutputMaker;
import com.matchandtrade.doc.util.RequestResponseHolder;
import com.matchandtrade.doc.util.RequestResponseUtil;
import com.matchandtrade.doc.util.TemplateUtil;


public class RestAuthenticationMaker implements OutputMaker {
	
	public static final String AUTHENTICATIONS_SNIPPET = "AUTHENTICATIONS_SNIPPET";
	public static final String BASE_URL = "/rest/v1/authentications/";

	@Override
	public String buildDocContent() {
		// AUTHENTICATIONS_SNIPPET
		RequestResponseHolder authentication = RequestResponseUtil.buildGetRequestResponse(BASE_URL);
		String template = TemplateUtil.buildTemplate(getDocLocation());
		String snippet = TemplateUtil.buildSnippet(authentication.getHttpRequest(), authentication.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATIONS_SNIPPET, snippet);
		return template;
	}

	@Override
	public String getDocLocation() {
		return "doc/rest/authentications.md";
	}

}
