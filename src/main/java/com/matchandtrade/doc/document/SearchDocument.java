
package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.clientapi.MatchAndTradeClient;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SearchDocument implements Document {
	private static final String SEARCH_POST_PLACEHOLDER = "SEARCH_POST_PLACEHOLDER";
	private static final String TRADE_ID_PLACEHOLDER = "TRADE_ID";

	private final MatchAndTradeClient clientApi;
	private String template;
	private UserJson user;

	public SearchDocument() {
		clientApi = new MatchAndTradeClient();
		user = clientApi.findUser().getResponse().as(UserJson.class);
		template = TemplateUtil.buildTemplate(contentFilePath());
	}

	@Override
	public String contentFilePath() {
		return "search.html";
	}

	@Override
	public String content() {
		TradeJson trade = new TradeJson();
		trade.setName("Trading books - " + System.currentTimeMillis());
		trade = clientApi.create(trade).getResponse().as(TradeJson.class);

		MembershipJson membership = clientApi.findMembershipByUserIdAndTradeIdAsMembership(
			user.getUserId(),
			trade.getTradeId());

		createAndListArticle(membership, "American Gods");
		createAndListArticle(membership, "Neverwhere");
		createAndListArticle(membership, "Stardust");
		createAndListArticle(membership, "The Sandman");

		// SEARCH_POST_PLACEHOLDER
		SpecificationParser parser = clientApi.searchByTradeId(trade.getTradeId());
		template = TemplateUtil.replacePlaceholder(template, TRADE_ID_PLACEHOLDER, trade.getTradeId().toString());
		template = TemplateUtil.replacePlaceholder(template, SEARCH_POST_PLACEHOLDER, parser.asHtmlSnippet());

		template = TemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

	private void createAndListArticle(MembershipJson membership, String articleName) {
		ArticleJson article = new ArticleJson();
		article.setName(articleName);
		article = clientApi.create(article).getResponse().as(ArticleJson.class);
		ListingJson listing = new ListingJson();
		listing.setMembershipId(membership.getMembershipId());
		listing.setArticleId(article.getArticleId());
		clientApi.create(listing);
	}

}
