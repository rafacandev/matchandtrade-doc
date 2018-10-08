package com.matchandtrade.doc.maker.rest;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.matchandtrade.doc.maker.TemplateHelper;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeJson.State;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static org.hamcrest.Matchers.*;


public class TradeRestDocMaker implements RestDocMaker {
	
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
		String template = TemplateHelper.buildTemplate(contentFilePath());

		// TRADES_POST_PLACEHOLDER
		SpecificationParser postParser = parsePost();
		template = TemplateHelper.replacePlaceholder(template, TRADES_POST_PLACEHOLDER, postParser.asHtmlSnippet());
		TradeJson tradeJson = postParser.getResponse().body().as(TradeJson.class);

		// TRADES_PUT_PLACEHOLDER
		SpecificationParser putParser = parsePut(tradeJson);
		template = TemplateUtil.replacePlaceholder(template, TRADES_PUT_PLACEHOLDER, putParser.asHtmlSnippet());
		tradeJson = putParser.getResponse().body().as(TradeJson.class);

		// TRADES_GET_PLACEHOLDER
		SpecificationParser getByIdParser = parseGetById(tradeJson);
		template = TemplateHelper.replacePlaceholder(template, TRADES_GET_PLACEHOLDER, getByIdParser.asHtmlSnippet());

		// TRADES_SEARCH_PLACEHOLDER
		SpecificationParser searchParser = parseSearch();
		template = TemplateHelper.replacePlaceholder(template, TRADES_SEARCH_PLACEHOLDER, searchParser.asHtmlSnippet());

		// TRADES_GET_ALL_PLACEHOLDER
		SpecificationParser getAllParser = getAllParser();
		template = TemplateHelper.replacePlaceholder(template, TRADES_GET_ALL_PLACEHOLDER, getAllParser.asHtmlSnippet());
		
		// TRADES_DELETE_PLACEHOLDER
		SpecificationParser deleteParser = parseDelete(tradeJson);
		template = TemplateHelper.replacePlaceholder(template, TRADES_DELETE_PLACEHOLDER, deleteParser.asHtmlSnippet());

		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateHelper.appendHeaderAndFooter(template);
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

	private SpecificationParser parseGetById(TradeJson tradeJson) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given().filter(filter).header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
				.get(MatchAndTradeRestUtil.tradesUrl(tradeJson.getTradeId()));
		parser.getResponse().then().statusCode(200).and().body("tradeId", equalTo(tradeJson.getTradeId()));
		return parser;
	}

	private SpecificationParser parsePut(TradeJson tradeJson) {
		Integer tradeId = tradeJson.getTradeId();
		tradeJson.setTradeId(null); // Set as null because we do not want to display in the documentation
		tradeJson.setLinks(null); // Set as null because we do not want to display in the documentation
		String tradeName = "Board games in Toronto - " + System.currentTimeMillis() + "Updated";
		tradeJson.setName(tradeName);
		tradeJson.setDescription("The event will take place at Toronto Convention Center");
		tradeJson.setState(State.MATCHING_ARTICLES);

		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
				.contentType(ContentType.JSON)
				.body(tradeJson)
				.put(MatchAndTradeRestUtil.tradesUrl(tradeId));
		parser.getResponse().then().statusCode(200).and().body("name", equalTo(tradeName));
		return parser;
	}

	private SpecificationParser parsePost() {
		TradeJson tradeJson = new TradeJson();
		String tradeName = "Board games location TBD - " + System.currentTimeMillis();
		tradeJson.setName(tradeName);
		tradeJson.setDescription("We will update the trade information once the description is defined");

		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
			.contentType(ContentType.JSON)
			.body(tradeJson)
			.post(MatchAndTradeRestUtil.tradesUrl() + "/");
		parser.getResponse().then().statusCode(201).and().body("", hasKey("tradeId"));
		return parser;
	}

}
