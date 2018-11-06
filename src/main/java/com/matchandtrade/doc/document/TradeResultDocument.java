package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.util.MatchAndTradeClient;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.*;
import io.restassured.http.ContentType;


public class TradeResultDocument implements Document {
	
	private static final String RESULTS_GET_CSV = "RESULTS_GET_CSV";
	private static final String RESULTS_GET_JSON = "RESULTS_GET_JSON";
	private static final String SAMPLE_ROW = "SAMPLE_ROW";

	private final MatchAndTradeClient primaryClientApi;
	private final MatchAndTradeClient secondaryClientApi;
	private final MatchAndTradeClient tertiaryClientApi;

	private UserJson primaryUser;
	private UserJson secondaryUser;
	private UserJson tertiaryUser;

	private String template;

	public TradeResultDocument() {
		primaryClientApi = new MatchAndTradeClient();
		primaryUser = primaryClientApi.findUser().getResponse().as(UserJson.class);
		secondaryClientApi = new MatchAndTradeClient();
		secondaryUser = secondaryClientApi.findUser().getResponse().as(UserJson.class);
		tertiaryClientApi = new MatchAndTradeClient();
		tertiaryUser = tertiaryClientApi.findUser().getResponse().as(UserJson.class);

		template = TemplateUtil.buildTemplate(contentFilePath());
	}

	@Override
	public String content() {
		TradeJson trade = buildTrade();

		// RESULTS_GET_JSON
		SpecificationParser jsonResultsParser = primaryClientApi.findTradeResult(trade.getTradeId(), ContentType.JSON.toString());
		template = TemplateUtil.replacePlaceholder(template, RESULTS_GET_JSON, jsonResultsParser.asHtmlSnippet());

		// RESULTS_GET_CSV
		SpecificationParser csvResultsParser = primaryClientApi.findTradeResult(trade.getTradeId());
		template = TemplateUtil.replacePlaceholder(template, RESULTS_GET_CSV, csvResultsParser.asHtmlSnippet());

		// SAMPLE_ROW
		String sampleRow = buildSampleRow(csvResultsParser);
		template = TemplateUtil.replacePlaceholder(template, SAMPLE_ROW, sampleRow);

		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

	@Override
	public String contentFilePath() {
		return "trade-results.html";
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
		// Primary user setup
		primaryUser.setName("Olavo");
		primaryClientApi.update(primaryUser);

		TradeJson trade = new TradeJson();
		trade.setName("Board games in Montreal - " + System.currentTimeMillis());
		trade = primaryClientApi.create(trade).getResponse().as(TradeJson.class);

		MembershipJson primaryMembership = primaryClientApi.findMembershipByUserIdAndTradeIdAsMembership(primaryUser.getUserId(), trade.getTradeId());

		ArticleJson primaryAplesToAplesArticle = createArticle(primaryClientApi, "Apples to Apples");
		listArticle(primaryClientApi, primaryMembership, primaryAplesToAplesArticle);

		ArticleJson primaryBoraBoraArticle = createArticle(primaryClientApi, "Bora bora");
		listArticle(primaryClientApi, primaryMembership, primaryBoraBoraArticle);

		// Secondary user setup
		secondaryUser.setName("Maria");
		secondaryClientApi.update(secondaryUser);

		MembershipJson secondaryMembership = new MembershipJson();
		secondaryMembership.setTradeId(trade.getTradeId());
		secondaryMembership.setUserId(secondaryUser.getUserId());
		secondaryMembership = secondaryClientApi.create(secondaryMembership).getResponse().as(MembershipJson.class);


		ArticleJson secondaryAndromedraArticle = createArticle(secondaryClientApi, "Andromedra");
		listArticle(secondaryClientApi, secondaryMembership, secondaryAndromedraArticle);
		ArticleJson secondaryBlokusArticle = createArticle(secondaryClientApi, "Blokus");
		listArticle(secondaryClientApi, secondaryMembership, secondaryBlokusArticle);
		ArticleJson secondaryCaylusArticle = createArticle(secondaryClientApi, "Caylus");
		listArticle(secondaryClientApi, secondaryMembership, secondaryCaylusArticle);

		// Create another trade member setup
		tertiaryUser.setName("Xavier");
		tertiaryClientApi.update(tertiaryUser);

		MembershipJson tertiaryMembership = new MembershipJson();
		tertiaryMembership.setUserId(tertiaryUser.getUserId());
		tertiaryMembership.setTradeId(trade.getTradeId());
		tertiaryMembership = tertiaryClientApi.create(tertiaryMembership).getResponse().as(MembershipJson.class);

		ArticleJson tertiaryAgricolaArticle = createArticle(tertiaryClientApi, "Agricola");
		listArticle(tertiaryClientApi, tertiaryMembership, tertiaryAgricolaArticle);

		trade.setState(TradeJson.State.MATCHING_ARTICLES);
		primaryClientApi.update(trade);

		offer(primaryClientApi, primaryMembership, primaryAplesToAplesArticle, secondaryAndromedraArticle);
		offer(primaryClientApi, primaryMembership, primaryAplesToAplesArticle, secondaryCaylusArticle);
		offer(primaryClientApi, primaryMembership, primaryBoraBoraArticle, secondaryAndromedraArticle);
		offer(primaryClientApi, primaryMembership, primaryBoraBoraArticle, secondaryBlokusArticle);
		offer(primaryClientApi, primaryMembership, primaryBoraBoraArticle, secondaryCaylusArticle);

		offer(secondaryClientApi, secondaryMembership, secondaryAndromedraArticle, primaryAplesToAplesArticle);
		offer(secondaryClientApi, secondaryMembership, secondaryBlokusArticle, primaryBoraBoraArticle);
		offer(secondaryClientApi, secondaryMembership, secondaryCaylusArticle, tertiaryAgricolaArticle);

		offer(tertiaryClientApi, tertiaryMembership, tertiaryAgricolaArticle, primaryAplesToAplesArticle);

		trade.setState(TradeJson.State.GENERATE_RESULTS);
		primaryClientApi.update(trade);

		return trade;
	}

	private ArticleJson createArticle(MatchAndTradeClient clientApi, String articleName) {
		ArticleJson article = new ArticleJson();
		article.setName(articleName);
		return clientApi.create(article).getResponse().as(ArticleJson.class);
	}

	private void listArticle(MatchAndTradeClient clientApi, MembershipJson membership, ArticleJson article) {
		ListingJson listing = new ListingJson();
		listing.setMembershipId(membership.getMembershipId());
		listing.setArticleId(article.getArticleId());
		clientApi.create(listing);
	}

	private SpecificationParser offer(MatchAndTradeClient clientApi,
	                                  MembershipJson membership,
	                                  ArticleJson offeredArticle,
	                                  ArticleJson wantedaArticle) {
		OfferJson offer = new OfferJson();
		offer.setOfferedArticleId(offeredArticle.getArticleId());
		offer.setWantedArticleId(wantedaArticle.getArticleId());
		return clientApi.create(membership.getMembershipId(), offer);
	}

}
