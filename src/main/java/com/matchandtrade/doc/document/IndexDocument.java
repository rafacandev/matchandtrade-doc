package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Date;

import static org.hamcrest.Matchers.notNullValue;

public class IndexDocument implements Document {

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
		matchAndTradeApiFacade.createTrade("Books in New York - " + new Date().getTime() + hashCode());
		matchAndTradeApiFacade.createTrade("Books in Paris - " + new Date().getTime() + hashCode());
		matchAndTradeApiFacade.createTrade("Books in Lima - " + new Date().getTime() + hashCode());

		SpecificationFilter filter = new SpecificationFilter();
		Response response = RestAssured.given()
			.filter(filter)
			.headers(MatchAndTradeRestUtil.getLastAuthorizationHeaderAsMap())
			.contentType(ContentType.JSON)
			.param("_pageNumber", 2)
			.param("_pageSize", 2)
			.get(MatchAndTradeRestUtil.tradesUrl());

		response.then().statusCode(200).and().body("[0].tradeId", notNullValue());

		SpecificationParser parser = new SpecificationParser(filter);
		template = TemplateUtil.replacePlaceholder(template, REST_GUIDE_PAGINATION, parser.asHtmlSnippet());
		return TemplateUtil.appendHeaderAndFooter(template);
	}

}
