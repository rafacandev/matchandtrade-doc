package com.matchandtrade.doc.util;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.UserJson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.*;

public class MatchAndTradeClient {

	private final Header authorizationHeader;
	private Integer userId;
//	private final String cookie;

	public MatchAndTradeClient() {
		SpecificationParser parser = authenticate();
		String authorizationHeaderName = "Authorization";
		String authorizationHeaderValue = parser.getResponse().getHeader(authorizationHeaderName);
//		this.cookie = parser.getResponse().getCookie("MTSESSION");
		this.authorizationHeader = new Header(authorizationHeaderName, authorizationHeaderValue);

		SpecificationParser authenticationsParser = findAuthentications();
		userId = authenticationsParser.getResponse().body().path("userId");
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
			.header(getAuthorizationHeader())
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

	public SpecificationParser findAuthentications() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(getAuthorizationHeader())
			.get(MatchAndTradeRestUtil.authenticationsUrl() + "/");
		parser.getResponse().then().statusCode(200).and().body("", hasKey("userId"));
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

	public SpecificationParser findUser() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
			.given()
			.filter(filter)
			.header(authorizationHeader)
			.get(MatchAndTradeRestUtil.usersUrl(userId));
		return parser;
	}

	public Header getAuthorizationHeader() {
		return authorizationHeader;
	}

	public SpecificationParser updateUser(UserJson user) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
				.contentType(ContentType.JSON)
				.body(user)
				.put(MatchAndTradeRestUtil.usersUrl(userId));
		parser.getResponse().then().statusCode(200).and().body("name", equalTo(user.getName()));
		return parser;
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
