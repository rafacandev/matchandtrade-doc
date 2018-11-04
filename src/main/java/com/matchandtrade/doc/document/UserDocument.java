
package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.util.MatchAndTradeClient;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.UserJson;

public class UserDocument implements Document {
	
	private static final String USERS_GET_PLACEHOLDER = "USERS_GET_PLACEHOLDER";
	private static final String USERS_PUT_PLACEHOLDER = "USERS_PUT_PLACEHOLDER";

	private MatchAndTradeClient clientApi;
	private String template;

	public UserDocument() {
		clientApi = new MatchAndTradeClient();
		template = TemplateUtil.buildTemplate(contentFilePath());
	}

	@Override
	public String content() {
		// USERS_PUT_PLACEHOLDER
		UserJson user = clientApi.findUser().getResponse().as(UserJson.class);
		user.setName("Scott Summers");
		SpecificationParser putUserParser = clientApi.update(user);
		template = TemplateUtil.replacePlaceholder(template, USERS_PUT_PLACEHOLDER, putUserParser.asHtmlSnippet());

		// USERS_GET_PLACEHOLDER
		SpecificationParser parser = clientApi.findUser();
		template = TemplateUtil.replacePlaceholder(template, USERS_GET_PLACEHOLDER, parser.asHtmlSnippet());

		return TemplateUtil.appendHeaderAndFooter(template);
	}

	@Override
	public String contentFilePath() {
		return "users.html";
	}

}
