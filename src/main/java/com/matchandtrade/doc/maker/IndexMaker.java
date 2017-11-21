package com.matchandtrade.doc.maker;

import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;

public class IndexMaker extends OutputMaker {

	public static final String REST_GUIDE_PAGINATION = "REST_GUIDE_PAGINATION";
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());

		// REST_GUIDE_PAGINATION
		MatchAndTradeApiFacade matchAndTradeApiFacade = new MatchAndTradeApiFacade();
		matchAndTradeApiFacade.createTrade("Books in New York");
		matchAndTradeApiFacade.createTrade("Books in Paris");
		matchAndTradeApiFacade.createTrade("Books in Lima");
		SnippetFactory snippetFactory = new SnippetFactory(MatchAndTradeRestUtil.getLastAuthorizationHeader());
		RequestSpecification requestSpecification = new RequestSpecBuilder()
				.addParam("_pageNumber", 2)
				.addParam("_pageSize", 2)
				.build();
		Snippet paginationSnippet = snippetFactory.makeSnippet(
				Method.GET,
				requestSpecification,
				MatchAndTradeRestUtil.tradesUrl()
		);

		template = TemplateUtil.replacePlaceholder(template, REST_GUIDE_PAGINATION, paginationSnippet.asHtml());

		return template;
	}

	@Override
	public String getDocLocation() {
		return "index.html";
	}
}
