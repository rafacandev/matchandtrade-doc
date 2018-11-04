package com.matchandtrade.doc.util;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.rest.v1.json.TradeJson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class MatchAndTradeClient {

	private final Header authenticationHeader;
//	private final String cookie;

	public MatchAndTradeClient() {
		SpecificationParser parser = authenticate();
		String authorizationHeaderName = "Authorization";
		String authorizationHeaderValue = parser.getResponse().getHeader(authorizationHeaderName);
//		this.cookie = parser.getResponse().getCookie("MTSESSION");
		this.authenticationHeader = new Header(authorizationHeaderName, authorizationHeaderValue);
	}

	public static SpecificationParser authenticate() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.get(MatchAndTradeRestUtil.authenticateUrl());
		// Assert status is redirect
		parser.getResponse().then().statusCode(302);
		return parser;
	}

	public SpecificationParser create(TradeJson trade) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
			.given()
			.filter(filter)
			.header(getAuthenticationHeader())
			.contentType(ContentType.JSON)
			.body(trade)
			.when()
			.post(MatchAndTradeRestUtil.tradesUrl() + "/");
		return parser;
	}

	/**
	 * Need to keep the same cookie between "authenticate" and "authenticate info"
	 *
	 * @param cookie
	 * @return
	 */
	public SpecificationParser findAuthenticationInfo(String cookie) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.header("cookie", cookie)
				.filter(filter)
				.get(MatchAndTradeRestUtil.authenticateInfoUrl());
		parser.getResponse().then().statusCode(200);
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

	public SpecificationParser singOff() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
				.get(MatchAndTradeRestUtil.signOffUrl());
		parser.getResponse().then().statusCode(205).header("Authorization", nullValue());
		return parser;
	}

}
