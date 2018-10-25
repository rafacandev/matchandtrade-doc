package com.matchandtrade.doc.maker.rest;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.matchandtrade.doc.maker.TemplateHelper;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.rest.v1.json.ArticleJson;
import com.matchandtrade.rest.v1.json.MembershipJson;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.UserJson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import java.util.Date;

import static org.hamcrest.Matchers.*;


public class TradeResultRestDocMaker implements RestDocMaker {
	
	private static final String RESULTS_GET_CSV = "RESULTS_GET_CSV";
	private static final String RESULTS_GET_JSON = "RESULTS_GET_JSON";
	private static final String SAMPLE_ROW = "SAMPLE_ROW";

	@Override
	public String contentFilePath() {
		return "trade-results.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());
		TradeJson trade = buildTrade();

		// RESULTS_GET_CSV
		SpecificationParser csvResultsParser = parseCsvResults(trade);
		template = TemplateHelper.replacePlaceholder(template, RESULTS_GET_CSV, csvResultsParser.asHtmlSnippet());

		// SAMPLE_ROW
		String sampleRow = buildSampleRow(csvResultsParser);
		template = TemplateHelper.replacePlaceholder(template, SAMPLE_ROW, sampleRow);

		// RESULTS_GET_JSON
		SpecificationParser jsonResultsParser = parseJsonResults(trade);
		template = TemplateHelper.replacePlaceholder(template, RESULTS_GET_JSON, jsonResultsParser.asHtmlSnippet());

		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

	private String buildSampleRow(SpecificationParser csvResultsParser) {
		String[] rows = csvResultsParser.getResponse().body().asString().split("\n");
		for (String row : rows) {
			if (row.contains("Olavo")
					&& row.contains("Apples to Apples")
					&& row.contains("RECEIVES")
					&& row.contains("Maria")
					&& row.contains("Caylus")
					&& row.contains("SENDS")
					&& row.contains("Xavier")) {
				return row;
			}
		}
		return null;
	}

	private TradeJson buildTrade() {
		// Create a trade owner setup
		MatchAndTradeApiFacade olavoApiFacade = new MatchAndTradeApiFacade();
		UserJson olavo = olavoApiFacade.getUser();
		olavo.setName("Olavo");
		olavoApiFacade.saveUser(olavo);
		TradeJson trade = olavoApiFacade.createTrade("Board games in Montreal - " + new Date().getTime() + this.hashCode());

		MembershipJson olavoMembership = olavoApiFacade.findMembershipByUserIdAndTradeId(MatchAndTradeRestUtil.getLastAuthenticatedUserId(), trade.getTradeId());

		ArticleJson applesToApples = olavoApiFacade.createArticle("Apples to Apples");
		int responseCode = olavoApiFacade.createListing(olavoMembership.getMembershipId(), applesToApples.getArticleId());
		ArticleJson beta = olavoApiFacade.createArticle("Bora Bora");
		responseCode = olavoApiFacade.createListing(olavoMembership.getMembershipId(), beta.getArticleId());

		// Create a trade member setup
		MatchAndTradeRestUtil.nextAuthorizationHeader();
		MatchAndTradeApiFacade mariaApiFacade = new MatchAndTradeApiFacade();
		UserJson maria = mariaApiFacade.getUser();
		maria.setName("Maria");
		mariaApiFacade.saveUser(maria);
		MembershipJson memberMembership = mariaApiFacade.subscribeToTrade(trade);
		ArticleJson andromedra = mariaApiFacade.createArticle("Andromedra");
		mariaApiFacade.createListing(memberMembership.getMembershipId(), andromedra.getArticleId());
		ArticleJson blokus = mariaApiFacade.createArticle("Blokus");
		mariaApiFacade.createListing(memberMembership.getMembershipId(), blokus.getArticleId());
		ArticleJson caylus = mariaApiFacade.createArticle("Caylus");
		mariaApiFacade.createListing(memberMembership.getMembershipId(), caylus.getArticleId());

		// Create another trade member setup
		MatchAndTradeRestUtil.nextAuthorizationHeader();
		MatchAndTradeApiFacade xavierApiFacade = new MatchAndTradeApiFacade();
		UserJson xavier = xavierApiFacade.getUser();
		xavier.setName("Xavier");
		xavierApiFacade.saveUser(xavier);
		MembershipJson xavierMembership = xavierApiFacade.subscribeToTrade(trade);
		ArticleJson agricola = xavierApiFacade.createArticle("Agricola");
		xavierApiFacade.createListing(xavierMembership.getMembershipId(), agricola.getArticleId());

		trade.setState(TradeJson.State.MATCHING_ARTICLES);
		olavoApiFacade.saveTrade(trade);

		olavoApiFacade.createOffer(olavoMembership.getMembershipId(), applesToApples.getArticleId(), andromedra.getArticleId());
		olavoApiFacade.createOffer(olavoMembership.getMembershipId(), applesToApples.getArticleId(), beta.getArticleId());
		olavoApiFacade.createOffer(olavoMembership.getMembershipId(), applesToApples.getArticleId(), caylus.getArticleId());
		olavoApiFacade.createOffer(olavoMembership.getMembershipId(), beta.getArticleId(), andromedra.getArticleId());
		olavoApiFacade.createOffer(olavoMembership.getMembershipId(), beta.getArticleId(), blokus.getArticleId());
		olavoApiFacade.createOffer(olavoMembership.getMembershipId(), beta.getArticleId(), caylus.getArticleId());

		mariaApiFacade.createOffer(memberMembership.getMembershipId(), andromedra.getArticleId(), applesToApples.getArticleId());
		mariaApiFacade.createOffer(memberMembership.getMembershipId(), blokus.getArticleId(), beta.getArticleId());
		mariaApiFacade.createOffer(memberMembership.getMembershipId(), caylus.getArticleId(), agricola.getArticleId());

		xavierApiFacade.createOffer(xavierMembership.getMembershipId(), agricola.getArticleId(), applesToApples.getArticleId());

		trade.setState(TradeJson.State.GENERATE_RESULTS);
		olavoApiFacade.saveTrade(trade);
		return trade;
	}

	private SpecificationParser parseCsvResults(TradeJson trade) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
				.contentType("text/csv")
				.get(MatchAndTradeRestUtil.tradeResultsUrl(trade.getTradeId()));
		return parser;
	}

	private SpecificationParser parseJsonResults(TradeJson trade) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
				.contentType(ContentType.JSON)
				.get(MatchAndTradeRestUtil.tradeResultsUrl(trade.getTradeId()));
		parser.getResponse().then()
				.body("totalOfArticles", equalTo(6))
				.body("totalOfTradedArticles", equalTo(5))
				.body("totalOfNotTradedArticles", equalTo(1));
		return parser;
	}

}
