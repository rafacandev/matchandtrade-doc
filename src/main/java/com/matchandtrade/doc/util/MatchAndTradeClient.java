package com.matchandtrade.doc.util;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.clientapi.Endpoint;
import com.matchandtrade.rest.v1.json.*;
import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.*;

public class MatchAndTradeClient {

	private static final Logger log = LoggerFactory.getLogger(MatchAndTradeClient.class);

	private final Header authorizationHeader;
	private Integer userId;

	public MatchAndTradeClient() {
		SpecificationParser parser = authenticate();
		String authorizationHeaderName = "Authorization";
		String authorizationHeaderValue = parser.getResponse().getHeader(authorizationHeaderName);
		this.authorizationHeader = new Header(authorizationHeaderName, authorizationHeaderValue);

		SpecificationParser authenticationsParser = findAuthentications();
		userId = authenticationsParser.getResponse().body().path("userId");
	}

	public static SpecificationParser authenticate() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.get(Endpoint.authenticate());
		logAndAssertStatus(parser, FOUND);
		return parser;
	}

	public SpecificationParser create(ArticleJson article) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
			.given()
			.filter(filter)
			.contentType(ContentType.JSON)
			.header(authorizationHeader)
			.body(article)
			.post(Endpoint.articles());
		logAndAssertStatus(parser, CREATED);
		return parser;
	}

	public SpecificationParser create(Integer articleId, Integer attachmentId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(authorizationHeader)
			.contentType(ContentType.JSON)
			.post(Endpoint.articleAttachments(articleId, attachmentId));
		logAndAssertStatus(parser, CREATED);
		return parser;
	}

	public SpecificationParser create(ListingJson listing) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
			.given()
			.filter(filter)
			.contentType(ContentType.JSON)
			.header(authorizationHeader)
			.body(listing)
			.post(Endpoint.listing());
		logAndAssertStatus(parser, CREATED);
		return parser;
	}

	public SpecificationParser create(MembershipJson membership) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(authorizationHeader)
			.contentType(ContentType.JSON)
			.body(membership)
			.post(Endpoint.memberships());
		logAndAssertStatus(parser, CREATED);
		return parser;
	}

	public SpecificationParser create(Integer membershipId, OfferJson offer) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
			.given()
			.filter(filter)
			.contentType(ContentType.JSON)
			.header(authorizationHeader)
			.body(offer)
			.post(Endpoint.offers(membershipId));
		logAndAssertStatus(parser, CREATED);
		return parser;
	}

	public SpecificationParser create(TradeJson trade) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
			.given()
			.filter(filter)
			.header(getAuthorizationHeader())
			.contentType(ContentType.JSON)
			.body(trade)
			.post(Endpoint.trades());
		logAndAssertStatus(parser, CREATED);
		return parser;
	}

	public SpecificationParser createAttachment(String filepathForAnImagePng) {
		String fileLocation = MatchAndTradeClient.class.getClassLoader().getResource(filepathForAnImagePng).getFile();
		File file = new File(fileLocation);
		MultiPartSpecification fileSpec = new MultiPartSpecBuilder(file).mimeType("image/png").fileName("my-image.png").build();
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.multiPart(fileSpec)
			.header(authorizationHeader)
			.post(Endpoint.attachments());
		logAndAssertStatus(parser, CREATED);
		return parser;
	}

	public SpecificationParser deleteArticle(Integer articleId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
			.given()
			.filter(filter)
			.header(authorizationHeader)
			.delete(Endpoint.articles(articleId));
		logAndAssertStatus(parser, NO_CONTENT);
		return parser;
	}

	public SpecificationParser deleteArticleAttachment(Integer articleId, Integer attachmentId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
			.given()
			.filter(filter)
			.header(authorizationHeader)
			.delete(Endpoint.articleAttachments(articleId, attachmentId));
		logAndAssertStatus(parser, NO_CONTENT);
		return parser;
	}

	public SpecificationParser deleteListing(ListingJson listing) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
			.given()
			.filter(filter)
			.contentType(ContentType.JSON)
			.header(authorizationHeader)
			.body(listing)
			.delete(Endpoint.listing());
		logAndAssertStatus(parser, NO_CONTENT);
		return parser;
	}

	public SpecificationParser deleteMembership(Integer membershipId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(authorizationHeader)
			.delete(Endpoint.memberships(membershipId));
		parser.getResponse().then().statusCode(204);
		return parser;
	}

	public SpecificationParser deleteOffer(Integer membershipId, Integer offerId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
			.given()
			.filter(filter)
			.contentType(ContentType.JSON)
			.header(authorizationHeader)
			.delete(Endpoint.offers(membershipId, offerId));
		parser.getResponse().then().statusCode(204);
		return parser;
	}

	public SpecificationParser deleteTrade(Integer tradeId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(authorizationHeader)
			.delete(Endpoint.trades(tradeId));
		logAndAssertStatus(parser, NO_CONTENT);
		return parser;
	}

	public static SpecificationParser findArticle(Integer articleId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
			.given()
			.filter(filter)
			.get(Endpoint.articles(articleId));
		logAndAssertStatus(parser, OK);
		return parser;
	}

	public static SpecificationParser findArticles() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
			.given()
			.filter(filter)
			.get(Endpoint.articles());
		logAndAssertStatus(parser, OK);
		return parser;
	}

	/**
	 * Need to keep the same cookie between "authenticate" and "authenticate info"
	 *
	 * @param cookie
	 * @return
	 */
	public SpecificationParser findAuthenticationInfo(String cookie) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.header("cookie", cookie)
			.filter(filter)
			.get(Endpoint.authenticateInfo());
		logAndAssertStatus(parser, OK);
		return parser;
	}

	public SpecificationParser findAuthentications() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(getAuthorizationHeader())
			.get(Endpoint.authentications());
		logAndAssertStatus(parser, OK);
		return parser;
	}

	public SpecificationParser findMembership(Integer membershipId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(authorizationHeader)
			.get(Endpoint.memberships(membershipId));
		logAndAssertStatus(parser, OK);
		return parser;
	}

	public SpecificationParser findMemberships(Integer pageNumber, Integer pageSize) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(authorizationHeader)
			.contentType(ContentType.JSON)
			.param("_pageNumber", pageNumber)
			.param("_pageSize", pageSize)
			.get(Endpoint.memberships());
		logAndAssertStatus(parser, OK);
		return parser;
	}

	public SpecificationParser findMembershipByUserIdOrTradeId(Integer userId, Integer tradeId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RequestSpecification request = RestAssured.given()
				.filter(filter)
				.header(authorizationHeader)
				.contentType(ContentType.JSON);
		if (userId != null) {
			request.queryParam("userId", userId);
		}
		if (userId != null) {
			request.queryParam("tradeId", tradeId);
		}
		request.get(Endpoint.memberships());
		logAndAssertStatus(parser, OK);
		return parser;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MembershipJson findMembershipByUserIdAndTradeIdAsMembership(Integer userId, Integer tradeId) {
		SpecificationParser parser = findMembershipByUserIdOrTradeId(userId, tradeId);
		List<Map> responseList = parser.getResponse().body().as(List.class);
		Map<String, Object> membershipAsMap = responseList.get(0);
		MembershipJson result = new MembershipJson();
		result.setUserId(userId);
		result.setTradeId(tradeId);
		result.setMembershipId(Integer.parseInt(membershipAsMap.get("membershipId").toString()));
		result.setType(MembershipJson.Type.valueOf(membershipAsMap.get("type").toString()));
		return result;
	}

	public SpecificationParser findOffer(Integer membershipId, Integer offerId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(authorizationHeader)
			.get(Endpoint.offers(membershipId, offerId));
		logAndAssertStatus(parser, OK);
		return parser;
	}

	public SpecificationParser findOffersByMembershipIdAndOfferedArticleIdAndWantedArticleId(Integer membershipId, Integer offeredArticleId, Integer wantedArticleId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RequestSpecification request = RestAssured.given()
				.filter(filter)
				.header(authorizationHeader);
		if (offeredArticleId != null) {
			request.queryParam("offeredArticleId", offeredArticleId);
		}
		if (wantedArticleId != null) {
			request.queryParam("wantedArticleId", wantedArticleId);
		}
		request.get(Endpoint.offers(membershipId));
		logAndAssertStatus(parser, OK);
		return parser;
	}

	public SpecificationParser findTrade(Integer tradeId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(authorizationHeader)
			.contentType(ContentType.JSON)
			.get(Endpoint.trades(tradeId));
		logAndAssertStatus(parser, OK);
		return parser;
	}

	public SpecificationParser findTradeResult(Integer tradeId) {
		return findTradeResult(tradeId, "text/csv");
	}

	public SpecificationParser findTradeResult(Integer tradeId, String contentType) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(authorizationHeader)
			.contentType(contentType)
			.get(Endpoint.tradeResults(tradeId));
		return parser;
	}

	private static void logAndAssertStatus(SpecificationParser parser, HttpStatus httpStatus) {
		log.debug("HTTP request/response:\n{}\n{}", parser.requestAsText(), parser.responseAsText());
		parser.getResponse().then().statusCode(httpStatus.value());
	}

	public static SpecificationParser findTrades(Integer pageNumber, Integer pageSize) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		Response response = RestAssured.given()
			.filter(filter)
			.contentType(ContentType.JSON)
			.param("_pageNumber", pageNumber)
			.param("_pageSize", pageSize)
			.get(Endpoint.trades());
		response.then().statusCode(200).and().body("[0].tradeId", notNullValue());
		return parser;
	}

	public static SpecificationParser findTrades() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.contentType(ContentType.JSON)
			.get(Endpoint.trades());
		logAndAssertStatus(parser, OK);
		return parser;
	}

	public SpecificationParser findUser() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
			.given()
			.filter(filter)
			.header(authorizationHeader)
			.get(Endpoint.users(userId));
		return parser;
	}

	public Header getAuthorizationHeader() {
		return authorizationHeader;
	}

	public Integer getUserId() {
		return userId;
	}

	public SpecificationParser update(ArticleJson article) {
		ArticleJson requestBody = new ArticleJson();
		BeanUtils.copyProperties(article, requestBody);
		requestBody.setArticleId(null);

		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
				.given()
				.filter(filter)
				.contentType(ContentType.JSON)
				.header(authorizationHeader)
				.body(article)
				.put(Endpoint.articles(article.getArticleId()));
		logAndAssertStatus(parser, OK);
		return parser;
	}

	public SpecificationParser update(TradeJson trade) {
		TradeJson requestBody = new TradeJson();
		BeanUtils.copyProperties(trade, requestBody);
		requestBody.setTradeId(null);

		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
				.given()
				.filter(filter)
				.header(getAuthorizationHeader())
				.contentType(ContentType.JSON)
				.body(requestBody)
				.put(Endpoint.trades(trade.getTradeId()));
		logAndAssertStatus(parser, OK);
		return parser;
	}

	public SpecificationParser update(UserJson user) {
		UserJson requestBody = new UserJson();
		BeanUtils.copyProperties(user, requestBody);
		requestBody.setUserId(null);

		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(authorizationHeader)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.put(Endpoint.users(userId));
		logAndAssertStatus(parser, OK);
		return parser;
	}

	public SpecificationParser singOff() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(authorizationHeader)
				.get(Endpoint.signOff());
		logAndAssertStatus(parser, RESET_CONTENT);
		return parser;
	}

}
