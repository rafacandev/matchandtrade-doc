package com.matchandtrade.doc.maker.rest;

import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.rest.v1.json.AttachmentJson;
import com.matchandtrade.rest.v1.json.ItemJson;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;

import io.restassured.http.ContentType;
import io.restassured.http.Method;


public class ItemAttachmentRestDocMaker implements RestDocMaker {
	
	private static final String POST_PLACEHOLDER = "POST_PLACEHOLDER";
	private static final String GET_ALL_PLACEHOLDER = "GET_ALL_PLACEHOLDER";
	private static final String DELETE_PLACEHOLDER = "DELETE_PLACEHOLDER";

	@Override
	public String contentFilePath() {
		return "item-attachments.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());
		
		MatchAndTradeApiFacade apiFacade = new MatchAndTradeApiFacade();
		AttachmentJson attachment = apiFacade.createAttachment("front-picture.png");
		
		SnippetFactory snippetFactory = new SnippetFactory(ContentType.JSON, MatchAndTradeRestUtil.getLastAuthorizationHeader());
		TradeJson trade = apiFacade.createTrade("Items with images - " + System.currentTimeMillis() + "" + hashCode());
		TradeMembershipJson membership = apiFacade.findTradeMembershipByUserIdAndTradeId(apiFacade.getUser().getUserId(), trade.getTradeId());
		ItemJson item = apiFacade.createItem(membership, "Item with images");
		
		Snippet addAttachmentToItem = snippetFactory.makeSnippet(Method.POST, MatchAndTradeRestUtil.attachmentsUrl(membership.getTradeMembershipId(), item.getArticleId(), attachment.getAttachmentId()));
		addAttachmentToItem.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, POST_PLACEHOLDER, addAttachmentToItem.asHtml());
		
		Snippet getAllAttachmentsOfItem = snippetFactory.makeSnippet(MatchAndTradeRestUtil.attachmentsUrl(membership.getTradeMembershipId(), item.getArticleId()));
		getAllAttachmentsOfItem.getResponse().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, GET_ALL_PLACEHOLDER, getAllAttachmentsOfItem.asHtml());
		
		Snippet deleteAttachmentFromItem = snippetFactory
				.makeSnippet(Method.DELETE, MatchAndTradeRestUtil.attachmentsUrl(membership.getTradeMembershipId(), item.getArticleId(), attachment.getAttachmentId()));
		deleteAttachmentFromItem.getResponse().then().statusCode(204);
		template = TemplateUtil.replacePlaceholder(template, DELETE_PLACEHOLDER, deleteAttachmentFromItem.asHtml());
		
		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

}
