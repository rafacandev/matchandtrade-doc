package com.matchandtrade.doc.maker.rest;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.rest.v1.json.ArticleJson;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.MembershipJson;
import com.matchandtrade.rest.v1.json.UserJson;

import io.restassured.http.ContentType;


public class TradeResultRestDocMaker implements RestDocMaker {
	
	private static final String RESULTS_GET = "RESULTS_GET";

	@Override
	public String contentFilePath() {
		return "trade-results.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());

		// Create a trade owner setup
		MatchAndTradeApiFacade olavoApiFacade = new MatchAndTradeApiFacade();
		SnippetFactory olavoSnippetFactory = new SnippetFactory(ContentType.JSON, MatchAndTradeRestUtil.getLastAuthorizationHeader());
		UserJson olavo = olavoApiFacade.getUser();
		olavo.setName("Olavo");
		olavoApiFacade.saveUser(olavo);
		TradeJson trade = olavoApiFacade.createTrade("Board games in Montreal - " + new Date().getTime() + this.hashCode());
		MembershipJson olavoMembership = olavoApiFacade.findMembershipByUserIdAndTradeId(MatchAndTradeRestUtil.getLastAuthenticatedUserId(), trade.getTradeId());
		ArticleJson applesToApples = olavoApiFacade.createArticle(olavoMembership, "Apples to Apples");
		ArticleJson beta = olavoApiFacade.createArticle(olavoMembership, "Bora Bora");

		// Create a trade member setup
		MatchAndTradeRestUtil.nextAuthorizationHeader();
		MatchAndTradeApiFacade mariaApiFacade = new MatchAndTradeApiFacade();
		UserJson maria = mariaApiFacade.getUser();
		maria.setName("Maria");
		mariaApiFacade.saveUser(maria);
		MembershipJson memberMembership = mariaApiFacade.subscribeToTrade(trade);
		ArticleJson andromedra = mariaApiFacade.createArticle(memberMembership, "Andromedra");
		ArticleJson blokus = mariaApiFacade.createArticle(memberMembership, "Blokus");
		ArticleJson caylus = mariaApiFacade.createArticle(memberMembership, "Caylus");

		// Create another trade member setup
		MatchAndTradeRestUtil.nextAuthorizationHeader();
		MatchAndTradeApiFacade xavierApiFacade = new MatchAndTradeApiFacade();
		UserJson xavier = xavierApiFacade.getUser();
		xavier.setName("Xavier");
		xavierApiFacade.saveUser(xavier);
		MembershipJson xavierMembership = xavierApiFacade.subscribeToTrade(trade);
		ArticleJson agricola = xavierApiFacade.createArticle(xavierMembership, "Agricola");
		
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
		
		Snippet getSnippet = olavoSnippetFactory.makeSnippet(MatchAndTradeRestUtil.tradeResultsUrl(trade.getTradeId()));
		assertTrue(getSnippet.getResponse().body().asString().contains("\"totalOfArticles\":6"));
		assertTrue(getSnippet.getResponse().body().asString().contains("\"totalOfTradedArticles\":5"));
		assertTrue(getSnippet.getResponse().body().asString().contains("\"totalOfNotTradedArticles\":1,"));
		template = TemplateUtil.replacePlaceholder(template, RESULTS_GET, getSnippet.asHtml());
		
		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

}
