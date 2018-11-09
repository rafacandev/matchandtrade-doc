
package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.clientapi.Endpoint;
import com.matchandtrade.doc.util.MatchAndTradeClient;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.*;
import com.matchandtrade.rest.v1.json.search.Recipe;
import com.matchandtrade.rest.v1.json.search.SearchCriteriaJson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static org.hamcrest.Matchers.equalTo;


public class SearchDocument implements Document {
	
	private static final String SEARCH_POST_PLACEHOLDER = "SEARCH_POST_PLACEHOLDER";
	private static final String TRADE_ID_PLACEHOLDER = "TRADE_ID";
	private static final String MEMBER_ID_PLACEHOLDER = "MEMBERSHIP_ID";

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
		SpecificationParser parser = postSearchParser(trade, membership);
		template = TemplateUtil.replacePlaceholder(template, TRADE_ID_PLACEHOLDER, trade.getTradeId().toString());
		template = TemplateUtil.replacePlaceholder(template, MEMBER_ID_PLACEHOLDER, membership.getMembershipId().toString());
		template = TemplateUtil.replacePlaceholder(template, SEARCH_POST_PLACEHOLDER, parser.asHtmlSnippet());

		template = PaginationTemplateUtil.replacePaginationTable(template);
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

	private SpecificationParser postSearchParser(TradeJson trade, MembershipJson secondMember) {
		SearchCriteriaJson search = new SearchCriteriaJson();
		search.setRecipe(Recipe.ARTICLES);
		search.addCriterion("Trade.tradeId", trade.getTradeId());
		search.addCriterion("Membership.membershipId", secondMember.getMembershipId());
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(clientApi.getAuthorizationHeader())
			.queryParam("_pageNumber", "2")
			.queryParam("_pageSize", "2")
			.contentType(ContentType.JSON)
			.body(search)
			.post(Endpoint.search());
		parser.getResponse().then().statusCode(200).and().header("X-Pagination-Total-Count", equalTo("4"));
		return parser;
	}

}
