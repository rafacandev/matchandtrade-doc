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
import com.matchandtrade.rest.v1.json.AttachmentJson;
import com.matchandtrade.rest.v1.json.MembershipJson;
import com.matchandtrade.rest.v1.json.TradeJson;
import io.restassured.RestAssured;


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
		TradeJson trade = apiFacade.createTrade("Articles with images - " + System.currentTimeMillis() + "" + hashCode());
		MembershipJson membership = apiFacade.findMembershipByUserIdAndTradeId(apiFacade.getUser().getUserId(), trade.getTradeId());
		ArticleJson article = apiFacade.createArticle(membership, "Article with images");

		SpecificationParser postParser = parsePostAttachment(attachment, membership, article);
		template = TemplateHelper.replacePlaceholder(template, POST_PLACEHOLDER, postParser.asHtmlSnippet());

		SpecificationParser getAllParser = parseGetAllAttachments(attachment, membership, article);
		template = TemplateHelper.replacePlaceholder(template, GET_ALL_PLACEHOLDER, getAllParser.asHtmlSnippet());

		SpecificationParser deleteParser = deleteAttachmentParser(attachment, membership, article);
		template = TemplateUtil.replacePlaceholder(template, DELETE_PLACEHOLDER, deleteParser.asHtmlSnippet());
		
		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

	private SpecificationParser deleteAttachmentParser(AttachmentJson attachment, MembershipJson membership, ArticleJson article) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
			.delete(MatchAndTradeRestUtil.attachmentsUrl(membership.getMembershipId(), article.getArticleId(), attachment.getAttachmentId()));
		parser.getResponse().then().statusCode(204);
		return parser;
	}

	private SpecificationParser parseGetAllAttachments(AttachmentJson attachment, MembershipJson membership, ArticleJson article) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
			.get(MatchAndTradeRestUtil.attachmentsUrl(membership.getMembershipId(), article.getArticleId()));
		parser.getResponse().then().statusCode(200);
		return parser;
	}

	private SpecificationParser parsePostAttachment(AttachmentJson attachment, MembershipJson membership, ArticleJson article) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
			.post(MatchAndTradeRestUtil.attachmentsUrl(membership.getMembershipId(), article.getArticleId(), attachment.getAttachmentId()));
		parser.getResponse().then().statusCode(201);
		return parser;
	}

}
