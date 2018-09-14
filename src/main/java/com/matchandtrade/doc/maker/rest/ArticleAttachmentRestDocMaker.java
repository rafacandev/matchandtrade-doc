package com.matchandtrade.doc.maker.rest;

import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.rest.v1.json.AttachmentJson;
import com.matchandtrade.rest.v1.json.ArticleJson;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.MembershipJson;

import io.restassured.http.ContentType;
import io.restassured.http.Method;


public class ArticleAttachmentRestDocMaker implements RestDocMaker {
	
	private static final String POST_PLACEHOLDER = "POST_PLACEHOLDER";
	private static final String GET_ALL_PLACEHOLDER = "GET_ALL_PLACEHOLDER";
	private static final String DELETE_PLACEHOLDER = "DELETE_PLACEHOLDER";

	@Override
	public String contentFilePath() {
		return "article-attachments.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());
		
		MatchAndTradeApiFacade apiFacade = new MatchAndTradeApiFacade();
		AttachmentJson attachment = apiFacade.createAttachment("front-picture.png");
		
		SnippetFactory snippetFactory = new SnippetFactory(ContentType.JSON, MatchAndTradeRestUtil.getLastAuthorizationHeader());
		TradeJson trade = apiFacade.createTrade("Articles with images - " + System.currentTimeMillis() + "" + hashCode());
		MembershipJson membership = apiFacade.findMembershipByUserIdAndTradeId(apiFacade.getUser().getUserId(), trade.getTradeId());
		ArticleJson article = apiFacade.createArticle(membership, "Article with images");
		
		Snippet addAttachmentToArticle = snippetFactory.makeSnippet(Method.POST, MatchAndTradeRestUtil.attachmentsUrl(membership.getMembershipId(), article.getArticleId(), attachment.getAttachmentId()));
		addAttachmentToArticle.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, POST_PLACEHOLDER, addAttachmentToArticle.asHtml());
		
		Snippet getAllAttachmentsOfArticle = snippetFactory.makeSnippet(MatchAndTradeRestUtil.attachmentsUrl(membership.getMembershipId(), article.getArticleId()));
		getAllAttachmentsOfArticle.getResponse().then().statusCode(200);
		template = TemplateUtil.replacePlaceholder(template, GET_ALL_PLACEHOLDER, getAllAttachmentsOfArticle.asHtml());
		
		Snippet deleteAttachmentFromArticle = snippetFactory
				.makeSnippet(Method.DELETE, MatchAndTradeRestUtil.attachmentsUrl(membership.getMembershipId(), article.getArticleId(), attachment.getAttachmentId()));
		deleteAttachmentFromArticle.getResponse().then().statusCode(204);
		template = TemplateUtil.replacePlaceholder(template, DELETE_PLACEHOLDER, deleteAttachmentFromArticle.asHtml());
		
		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

}
