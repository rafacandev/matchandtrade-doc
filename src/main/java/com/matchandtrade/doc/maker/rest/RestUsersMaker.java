package com.matchandtrade.doc.maker.rest;

import com.matchandtrade.doc.maker.OutputMaker;
import com.matchandtrade.doc.util.RequestResponseHolder;
import com.matchandtrade.doc.util.RestUtil;
import com.matchandtrade.doc.util.RequestResponseUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.UserJson;


public class RestUsersMaker implements OutputMaker {
	
	private static final String USERS_GET_SNIPPET = "USERS_GET_SNIPPET";
	private static final String USERS_PUT_SNIPPET = "USERS_PUT_SNIPPET";
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());

		// USERS_PUT_SNIPPET
		UserJson userJson = new UserJson();
		userJson.setEmail(RestUtil.getAuthenticatedUser().getEmail());
		userJson.setName("Scott Summers");
		RequestResponseHolder put = RequestResponseUtil.buildPutRequestResponse("/rest/v1/users/" + RestUtil.getAuthenticatedUser().getUserId(), userJson);
		String putSnippet = TemplateUtil.buildSnippet(put.getHttpRequest(), put.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, USERS_PUT_SNIPPET, putSnippet);

		// USERS_GET_SNIPPET
		RequestResponseHolder get = RequestResponseUtil.buildGetRequestResponse("/rest/v1/users/" + RestUtil.getAuthenticatedUser().getUserId());
		String getSnippet = TemplateUtil.buildSnippet(get.getHttpRequest(), get.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, USERS_GET_SNIPPET, getSnippet);
		
		return template;
	}

	@Override
	public String getDocLocation() {
		return "doc/rest/users.md";
	}

}
