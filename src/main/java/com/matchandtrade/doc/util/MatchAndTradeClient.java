package com.matchandtrade.doc.util;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.rest.v1.json.TradeJson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.notNullValue;

public class MatchAndTradeClient {

	private final Header authenticationHeader;

	public MatchAndTradeClient(Header authenticationHeader) {
		this.authenticationHeader = authenticationHeader;
	}

	public SpecificationParser create(TradeJson trade) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
			.given()
			.filter(filter)
			.header(authenticationHeader)
			.contentType(ContentType.JSON)
			.body(trade)
			.when()
			.post(MatchAndTradeRestUtil.tradesUrl() + "/");
		return parser;
	}

	public static SpecificationParser findTrades() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		Response response = RestAssured.given()
				.filter(filter)
				.headers(MatchAndTradeRestUtil.getLastAuthorizationHeaderAsMap())
				.contentType(ContentType.JSON)
				.param("_pageNumber", 2)
				.param("_pageSize", 2)
				.get(MatchAndTradeRestUtil.tradesUrl());
		response.then().statusCode(200).and().body("[0].tradeId", notNullValue());
		return parser;
	}

	public Header getAuthenticationHeader() {
		return authenticationHeader;
	}

}
