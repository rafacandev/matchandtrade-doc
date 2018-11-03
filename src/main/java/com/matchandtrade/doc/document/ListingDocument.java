package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.ArticleJson;
import com.matchandtrade.rest.v1.json.ListingJson;
import com.matchandtrade.rest.v1.json.MembershipJson;
import com.matchandtrade.rest.v1.json.TradeJson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;

public class ListingDocument implements Document {
	
	private static final String LISTING_POST_PLACEHOLDER = "LISTING_POST_PLACEHOLDER";
	private static final String LISTING_DELETE_PLACEHOLDER = "LISTING_DELETE_PLACEHOLDER";

	@Override
	public String contentFilePath() {
		return "listing.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());
		Header authenticationHeader = MatchAndTradeRestUtil.getLastAuthorizationHeader();
		TradeJson trade = buildTrade(authenticationHeader);
		MembershipJson membership = buildMembership(authenticationHeader, trade);
		ArticleJson article = buildArticle();

		// LISTING_POST_PLACEHOLDER
		SpecificationParser listingPostParser = ListingDocument.buildPostListingParser(authenticationHeader, membership, article);
		template = TemplateUtil.replacePlaceholder(template, LISTING_POST_PLACEHOLDER, listingPostParser.asHtmlSnippet());

		// LISTING_DELETE_PLACEHOLDER
		SpecificationParser listingDeleteParser = buildDeleteParser(authenticationHeader, membership, article);
		template = TemplateUtil.replacePlaceholder(template, LISTING_DELETE_PLACEHOLDER, listingDeleteParser.asHtmlSnippet());

		return TemplateUtil.appendHeaderAndFooter(template);
	}

	public static SpecificationParser buildPostListingParser(Header authenticationHeader, MembershipJson membership, ArticleJson article) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		ListingJson requestBody = new ListingJson();
		requestBody.setArticleId(article.getArticleId());
		requestBody.setMembershipId(membership.getMembershipId());
		RestAssured.given()
				.filter(filter)
				.header(authenticationHeader)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.post(MatchAndTradeRestUtil.listingUrl(membership.getMembershipId(), article.getArticleId()));
		return parser;
	}

	private SpecificationParser buildDeleteParser(Header authenticationHeader, MembershipJson membership, ArticleJson article) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		ListingJson requestBody = new ListingJson();
		requestBody.setArticleId(article.getArticleId());
		requestBody.setMembershipId(membership.getMembershipId());
		RestAssured.given()
				.filter(filter)
				.header(authenticationHeader)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.delete(MatchAndTradeRestUtil.listingUrl(membership.getMembershipId(), article.getArticleId()));
		return parser;
	}

	private ArticleJson buildArticle() {
		ArticleJson article = new ArticleJson();
		article.setName("Love Letter");
		article.setDescription("First edition in great condition");
		SpecificationParser parser = ArticleDocument.buildPostParser(article);
		article = parser.getResponse().as(ArticleJson.class);
		return article;
	}

	public static MembershipJson buildMembership(Header authenticationHeader, TradeJson trade) {
		SpecificationParser parser = MembershipDocument.buildSearchMembershipParser(
				MatchAndTradeRestUtil.getLastAuthenticatedUserId(),
				trade.getTradeId(),
				authenticationHeader);
		List<Map<String, Object>> response = parser.getResponse().as(List.class);
		Map<String, Object> membershipMap = response.get(0);
		MembershipJson result = new MembershipJson();
		result.setUserId(Integer.parseInt(membershipMap.get("userId").toString()));
		result.setTradeId(Integer.parseInt(membershipMap.get("tradeId").toString()));
		result.setMembershipId(Integer.parseInt(membershipMap.get("membershipId").toString()));
		return result;
	}

	private TradeJson buildTrade(Header authenticationHeader) {
		SpecificationParser tradeParser = TradeDocument.buildPostParser(authenticationHeader);
		return tradeParser.getResponse().as(TradeJson.class);
	}

}
