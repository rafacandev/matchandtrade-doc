package com.matchandtrade.doc.maker;

import static org.hamcrest.Matchers.notNullValue;

import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;

public class IndexRestDocMaker implements RestDocMaker {

	public static final String REST_GUIDE_PAGINATION = "REST_GUIDE_PAGINATION";

	@Override
	public String contentFilePath() {
		return "index.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());

		// REST_GUIDE_PAGINATION
		MatchAndTradeApiFacade matchAndTradeApiFacade = new MatchAndTradeApiFacade();
		matchAndTradeApiFacade.createTrade("Books in New York");
		matchAndTradeApiFacade.createTrade("Books in Paris");
		matchAndTradeApiFacade.createTrade("Books in Lima");
		RequestSpecification requestSpecification = new RequestSpecBuilder()
				.addHeaders(MatchAndTradeRestUtil.getLastAuthorizationHeaderAsMap())
				.setContentType(ContentType.JSON)
				.addParam("_pageNumber", 2)
				.addParam("_pageSize", 2)
				.build();
		Snippet paginationSnippet = SnippetFactory.makeSnippet(
				Method.GET,
				requestSpecification,
				MatchAndTradeRestUtil.tradesUrl()
		);
		paginationSnippet.getResponse().then().statusCode(200).and().body("[0].tradeId", notNullValue());

		template = TemplateUtil.replacePlaceholder(template, REST_GUIDE_PAGINATION, paginationSnippet.asHtml());

		return TemplateUtil.appendHeaderAndFooter(template);
	}

}
