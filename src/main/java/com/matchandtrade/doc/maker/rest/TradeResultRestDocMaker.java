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

		olavoApiFacade.createOffer(olavoMembership.getTradeMembershipId(), applesToApples.getItemId(), andromedra.getItemId());
		olavoApiFacade.createOffer(olavoMembership.getTradeMembershipId(), applesToApples.getItemId(), beta.getItemId());
		olavoApiFacade.createOffer(olavoMembership.getTradeMembershipId(), applesToApples.getItemId(), caylus.getItemId());
		olavoApiFacade.createOffer(olavoMembership.getTradeMembershipId(), beta.getItemId(), andromedra.getItemId());
		olavoApiFacade.createOffer(olavoMembership.getTradeMembershipId(), beta.getItemId(), blokus.getItemId());
		olavoApiFacade.createOffer(olavoMembership.getTradeMembershipId(), beta.getItemId(), caylus.getItemId());
		
		mariaApiFacade.createOffer(memberMembership.getTradeMembershipId(), andromedra.getItemId(), applesToApples.getItemId());
		mariaApiFacade.createOffer(memberMembership.getTradeMembershipId(), blokus.getItemId(), beta.getItemId());
		mariaApiFacade.createOffer(memberMembership.getTradeMembershipId(), caylus.getItemId(), agricola.getItemId());
		
		xavierApiFacade.createOffer(xavierMembership.getTradeMembershipId(), agricola.getItemId(), applesToApples.getItemId());

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
