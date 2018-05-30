
package com.matchandtrade.doc.maker.rest;

import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.rest.v1.json.FileJson;
import com.matchandtrade.rest.v1.json.ItemJson;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;

import io.restassured.http.ContentType;
import io.restassured.http.Method;


public class ItemFileRestDocMaker implements RestDocMaker {
	
	private static final String POST_PLACEHOLDER = "POST_PLACEHOLDER";
	private static final String GET_ALL_PLACEHOLDER = "GET_ALL_PLACEHOLDER";

	@Override
	public String contentFilePath() {
		return "item-files.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());
		
		MatchAndTradeApiFacade apiFacade = new MatchAndTradeApiFacade();
		FileJson file = apiFacade.createFile("front-picture.png");
		
		SnippetFactory snippetFactory = new SnippetFactory(ContentType.JSON, MatchAndTradeRestUtil.getLastAuthorizationHeader());
		TradeJson trade = apiFacade.createTrade("Items with images - " + System.currentTimeMillis() + "" + hashCode());
		TradeMembershipJson membership = apiFacade.findTradeMembershipByUserIdAndTradeId(apiFacade.getUser().getUserId(), trade.getTradeId());
		ItemJson item = apiFacade.createItem(membership, "Item with images");
		
		Snippet addFileToItem = snippetFactory.makeSnippet(Method.POST, MatchAndTradeRestUtil.filesUrl(membership.getTradeMembershipId(), item.getItemId(), file.getFileId()));
		addFileToItem.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, POST_PLACEHOLDER, addFileToItem.asHtml());
		
		Snippet getAllFilesOfItem = snippetFactory.makeSnippet(MatchAndTradeRestUtil.filesUrl(membership.getTradeMembershipId(), item.getItemId()));
		getAllFilesOfItem.getResponse().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, GET_ALL_PLACEHOLDER, getAllFilesOfItem.asHtml());
		
		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

}
