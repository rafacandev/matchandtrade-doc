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
import com.matchandtrade.rest.v1.json.ItemJson;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;
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
		TradeMembershipJson olavoMembership = olavoApiFacade.findTradeMembershipByUserIdAndTradeId(MatchAndTradeRestUtil.getLastAuthenticatedUserId(), trade.getTradeId());
		ItemJson applesToApples = olavoApiFacade.createItem(olavoMembership, "Apples to Apples");
		ItemJson beta = olavoApiFacade.createItem(olavoMembership, "Bora Bora");

		// Create a trade member setup
		MatchAndTradeRestUtil.nextAuthorizationHeader();
		MatchAndTradeApiFacade mariaApiFacade = new MatchAndTradeApiFacade();
		UserJson maria = mariaApiFacade.getUser();
		maria.setName("Maria");
		mariaApiFacade.saveUser(maria);
		TradeMembershipJson memberMembership = mariaApiFacade.subscribeToTrade(trade);
		ItemJson andromedra = mariaApiFacade.createItem(memberMembership, "Andromedra");
		ItemJson blokus = mariaApiFacade.createItem(memberMembership, "Blokus");
		ItemJson caylus = mariaApiFacade.createItem(memberMembership, "Caylus");

		// Create another trade member setup
		MatchAndTradeRestUtil.nextAuthorizationHeader();
		MatchAndTradeApiFacade xavierApiFacade = new MatchAndTradeApiFacade();
		UserJson xavier = xavierApiFacade.getUser();
		xavier.setName("Xavier");
		xavierApiFacade.saveUser(xavier);
		TradeMembershipJson xavierMembership = xavierApiFacade.subscribeToTrade(trade);
		ItemJson agricola = xavierApiFacade.createItem(xavierMembership, "Agricola");
		
		trade.setState(TradeJson.State.MATCHING_ITEMS);
		olavoApiFacade.saveTrade(trade);

		olavoApiFacade.createOffer(olavoMembership.getTradeMembershipId(), applesToApples.getArticleId(), andromedra.getArticleId());
		olavoApiFacade.createOffer(olavoMembership.getTradeMembershipId(), applesToApples.getArticleId(), beta.getArticleId());
		olavoApiFacade.createOffer(olavoMembership.getTradeMembershipId(), applesToApples.getArticleId(), caylus.getArticleId());
		olavoApiFacade.createOffer(olavoMembership.getTradeMembershipId(), beta.getArticleId(), andromedra.getArticleId());
		olavoApiFacade.createOffer(olavoMembership.getTradeMembershipId(), beta.getArticleId(), blokus.getArticleId());
		olavoApiFacade.createOffer(olavoMembership.getTradeMembershipId(), beta.getArticleId(), caylus.getArticleId());
		
		mariaApiFacade.createOffer(memberMembership.getTradeMembershipId(), andromedra.getArticleId(), applesToApples.getArticleId());
		mariaApiFacade.createOffer(memberMembership.getTradeMembershipId(), blokus.getArticleId(), beta.getArticleId());
		mariaApiFacade.createOffer(memberMembership.getTradeMembershipId(), caylus.getArticleId(), agricola.getArticleId());
		
		xavierApiFacade.createOffer(xavierMembership.getTradeMembershipId(), agricola.getArticleId(), applesToApples.getArticleId());

		trade.setState(TradeJson.State.GENERATE_RESULTS);
		olavoApiFacade.saveTrade(trade);
		
		Snippet getSnippet = olavoSnippetFactory.makeSnippet(MatchAndTradeRestUtil.tradeResultsUrl(trade.getTradeId()));
		assertTrue(getSnippet.getResponse().body().asString().contains("\"totalOfItems\":6"));
		assertTrue(getSnippet.getResponse().body().asString().contains("\"totalOfTradedItems\":5"));
		assertTrue(getSnippet.getResponse().body().asString().contains("\"totalOfNotTradedItems\":1,"));
		template = TemplateUtil.replacePlaceholder(template, RESULTS_GET, getSnippet.asHtml());
		
		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

}
