package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.clientapi.MatchAndTradeClient;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeJson.State;


public class TradeDocument implements Document {
	
	private static final String TRADES_POST_PLACEHOLDER = "TRADES_POST_PLACEHOLDER";
	private static final String TRADES_PUT_PLACEHOLDER = "TRADES_PUT_PLACEHOLDER";	
	private static final String TRADES_GET_PLACEHOLDER = "TRADES_GET_PLACEHOLDER";
	private static final String TRADES_DELETE_PLACEHOLDER = "TRADES_DELETE_PLACEHOLDER";
	private static final String TRADES_SEARCH_PLACEHOLDER = "TRADES_SEARCH_PLACEHOLDER";
	private static final String TRADES_GET_ALL_PLACEHOLDER = "TRADES_GET_ALL_PLACEHOLDER";

	private final MatchAndTradeClient clientApi;
	private String template;

	public TradeDocument() {
		clientApi = new MatchAndTradeClient();
		template = TemplateUtil.buildTemplate(contentFilePath());
	}

	@Override
	public String content() {
		// TRADES_POST_PLACEHOLDER
		TradeJson trade = buildTrade();
		SpecificationParser postParser = clientApi.create(trade);
		template = TemplateUtil.replacePlaceholder(template, TRADES_POST_PLACEHOLDER, postParser.asHtmlSnippet());
		trade = postParser.getResponse().body().as(TradeJson.class);

		// TRADES_PUT_PLACEHOLDER
		trade.setName(trade.getName() + " Updated");
		trade.setState(State.GENERATE_RESULTS);
		SpecificationParser putParser = clientApi.update(trade);
		template = TemplateUtil.replacePlaceholder(template, TRADES_PUT_PLACEHOLDER, putParser.asHtmlSnippet());

		// TRADES_GET_PLACEHOLDER
		SpecificationParser getByIdParser = clientApi.findTrade(trade.getTradeId());
		template = TemplateUtil.replacePlaceholder(template, TRADES_GET_PLACEHOLDER, getByIdParser.asHtmlSnippet());

		// TRADES_SEARCH_PLACEHOLDER
		SpecificationParser searchParser = MatchAndTradeClient.findTrades(2,2);
		template = TemplateUtil.replacePlaceholder(template, TRADES_SEARCH_PLACEHOLDER, searchParser.asHtmlSnippet());

		// TRADES_GET_ALL_PLACEHOLDER
		SpecificationParser getAllParser = MatchAndTradeClient.findTrades();
		template = TemplateUtil.replacePlaceholder(template, TRADES_GET_ALL_PLACEHOLDER, getAllParser.asHtmlSnippet());

		// TRADES_DELETE_PLACEHOLDER
		SpecificationParser deleteParser = clientApi.deleteTrade(trade.getTradeId());
		template = TemplateUtil.replacePlaceholder(template, TRADES_DELETE_PLACEHOLDER, deleteParser.asHtmlSnippet());

		template = TemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

	@Override
	public String contentFilePath() {
		return "trades.html";
	}

	private static TradeJson buildTrade() {
		TradeJson tradeJson = new TradeJson();
		String tradeName = "Board games in Toronto - " + System.currentTimeMillis();
		tradeJson.setName(tradeName);
		tradeJson.setDescription("More information to come.");
		return tradeJson;
	}

}
