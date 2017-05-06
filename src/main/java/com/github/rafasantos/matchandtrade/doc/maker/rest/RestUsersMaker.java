package com.github.rafasantos.matchandtrade.doc.maker.rest;

import com.github.rafasantos.matchandtrade.doc.maker.OutputMaker;
import com.github.rafasantos.matchandtrade.doc.util.RequestResponseHolder;
import com.github.rafasantos.matchandtrade.doc.util.RestUtil;
import com.github.rafasantos.matchandtrade.doc.util.SnippetUtil;
import com.github.rafasantos.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.UserJson;


public class RestUsersMaker implements OutputMaker {
	
	private static final String USERS_GET_SNIPPET = "USERS_GET_SNIPPET";
	private static final String USERS_PUT_SNIPPET = "USERS_PUT_SNIPPET";
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());

		RequestResponseHolder get = SnippetUtil.buildGetRequestResponse("/rest/v1/users/" + RestUtil.getAuthenticatedUser().getUserId());
		String getSnippet = TemplateUtil.buildSnippet(get.getHttpRequest(), get.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, USERS_GET_SNIPPET, getSnippet);

		UserJson requestJson = new UserJson();
		requestJson.setEmail(RestUtil.getAuthenticatedUser().getEmail());
		requestJson.setName("User name for PUT method");
		RequestResponseHolder put = SnippetUtil.buildPutRequestResponse("/rest/v1/users/" + RestUtil.getAuthenticatedUser().getUserId(), requestJson);
		String putSnippet = TemplateUtil.buildSnippet(put.getHttpRequest(), put.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, USERS_PUT_SNIPPET, putSnippet);
		
		return template;
	}

	@Override
	public String getDocLocation() {
		return "doc/rest/users.md";
	}

}
