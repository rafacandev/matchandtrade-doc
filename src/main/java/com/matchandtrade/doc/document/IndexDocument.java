package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.util.MatchAndTradeClient;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.TradeJson;

public class IndexDocument implements Document {

	public static final String REST_GUIDE_PAGINATION = "REST_GUIDE_PAGINATION";

	private String template;
	private MatchAndTradeClient clientApi;

	public IndexDocument() {
		clientApi = new MatchAndTradeClient();
		template = TemplateUtil.buildTemplate(contentFilePath());
	}

	@Override
	public String content() {
		// REST_GUIDE_PAGINATION
		createTrade("Books in Berlin " + System.currentTimeMillis());
		createTrade("Books in Paris " + System.currentTimeMillis());
		createTrade("Books in Lima " + System.currentTimeMillis());
		SpecificationParser findTradesParser = MatchAndTradeClient.findTrades();
		template = TemplateUtil.replacePlaceholder(template, REST_GUIDE_PAGINATION, findTradesParser.asHtmlSnippet());

		return TemplateUtil.appendHeaderAndFooter(template);
	}

	private void createTrade(String name) {
		TradeJson trade = new TradeJson();
		trade.setName(name);
		clientApi.create(trade);
	}

	@Override
	public String contentFilePath() {
		return "index.html";
	}

}
