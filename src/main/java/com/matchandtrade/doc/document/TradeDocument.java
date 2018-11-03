package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeJson.State;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.springframework.beans.BeanUtils;

import static org.hamcrest.Matchers.*;


public class TradeDocument implements Document {
	
	private static final String TRADES_POST_PLACEHOLDER = "TRADES_POST_PLACEHOLDER";
	private static final String TRADES_PUT_PLACEHOLDER = "TRADES_PUT_PLACEHOLDER";	
	private static final String TRADES_GET_PLACEHOLDER = "TRADES_GET_PLACEHOLDER";
	private static final String TRADES_DELETE_PLACEHOLDER = "TRADES_DELETE_PLACEHOLDER";
	private static final String TRADES_SEARCH_PLACEHOLDER = "TRADES_SEARCH_PLACEHOLDER";
	private static final String TRADES_GET_ALL_PLACEHOLDER = "TRADES_GET_ALL_PLACEHOLDER";
	
	@Override
	public String contentFilePath() {
		return "trades.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());

		// TRADES_POST_PLACEHOLDER
		SpecificationParser postParser = buildPostParser(MatchAndTradeRestUtil.getLastAuthorizationHeader());
		template = TemplateUtil.replacePlaceholder(template, TRADES_POST_PLACEHOLDER, postParser.asHtmlSnippet());
		TradeJson tradeJson = postParser.getResponse().body().as(TradeJson.class);

		// TRADES_PUT_PLACEHOLDER
		tradeJson.setName("Board games in Toronto - " + System.currentTimeMillis() + "Updated");
		tradeJson.setState(State.GENERATE_RESULTS);
		SpecificationParser putParser = TradeDocument.buildPutParser(
				MatchAndTradeRestUtil.getLastAuthorizationHeader(),
				tradeJson);
		template = TemplateUtil.replacePlaceholder(template, TRADES_PUT_PLACEHOLDER, putParser.asHtmlSnippet());
		tradeJson = putParser.getResponse().body().as(TradeJson.class);

		// TRADES_GET_PLACEHOLDER
		SpecificationParser getByIdParser = buildGetParser(tradeJson);
		template = TemplateUtil.replacePlaceholder(template, TRADES_GET_PLACEHOLDER, getByIdParser.asHtmlSnippet());

		// TRADES_SEARCH_PLACEHOLDER
		SpecificationParser searchParser = parseSearch();
		template = TemplateUtil.replacePlaceholder(template, TRADES_SEARCH_PLACEHOLDER, searchParser.asHtmlSnippet());

		// TRADES_GET_ALL_PLACEHOLDER
		SpecificationParser getAllParser = getAllParser();
		template = TemplateUtil.replacePlaceholder(template, TRADES_GET_ALL_PLACEHOLDER, getAllParser.asHtmlSnippet());
		
		// TRADES_DELETE_PLACEHOLDER
		SpecificationParser deleteParser = parseDelete(tradeJson);
		template = TemplateUtil.replacePlaceholder(template, TRADES_DELETE_PLACEHOLDER, deleteParser.asHtmlSnippet());

		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

	private SpecificationParser parseDelete(TradeJson tradeJson) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
			.delete(MatchAndTradeRestUtil.tradesUrl(tradeJson.getTradeId()));
		parser.getResponse().then().statusCode(204);
		return parser;
	}

	private SpecificationParser getAllParser() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(MatchAndTradeRestUtil
			.getLastAuthorizationHeader())
			.get(MatchAndTradeRestUtil.tradesUrl());
		parser.getResponse().then().statusCode(200).and().headers("X-Pagination-Total-Count", notNullValue());
		return parser;
	}

	private SpecificationParser parseSearch() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given().filter(filter).header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
				.queryParam("_pageNumber", "1")
				.queryParam("_pageSize", "3")
				.get(MatchAndTradeRestUtil.tradesUrl());
		parser.getResponse().then().statusCode(200);
		return parser;
	}

	private SpecificationParser buildGetParser(TradeJson tradeJson) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given().filter(filter).header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
				.get(MatchAndTradeRestUtil.tradesUrl(tradeJson.getTradeId()));
		parser.getResponse().then().statusCode(200).and().body("tradeId", equalTo(tradeJson.getTradeId()));
		return parser;
	}

	public static SpecificationParser buildPutParser(Header authorizationHeader, final TradeJson trade) {
		TradeJson requestBody = new TradeJson();
		BeanUtils.copyProperties(trade, requestBody);
		Integer tradeId = trade.getTradeId();
		requestBody.setTradeId(null); // Set as null because we do not want to display in the documentation
		requestBody.setLinks(null); // Set as null because we do not want to display in the documentation
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(authorizationHeader)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.put(MatchAndTradeRestUtil.tradesUrl(tradeId));
		parser.getResponse().then().statusCode(200).and().body("name", equalTo(trade.getName()));
		return parser;
	}

	public static SpecificationParser buildPostParser(Header authenticationHeader) {
		TradeJson tradeJson = new TradeJson();
		String tradeName = "Board games in Toronto - " + System.currentTimeMillis();
		tradeJson.setName(tradeName);
		tradeJson.setDescription("More information to come.");

		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(authenticationHeader)
			.contentType(ContentType.JSON)
			.body(tradeJson)
			.post(MatchAndTradeRestUtil.tradesUrl() + "/");
		parser.getResponse().then().statusCode(201).and().body("", hasKey("tradeId"));
		return parser;
	}

}
