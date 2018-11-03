package com.matchandtrade.doc.maker;

import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.maker.rest.*;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.rest.v1.json.*;
import io.restassured.http.Header;

public class UseCaseRestDocMaker implements DocumentContent {
	
	private static final String MEMBER_AUTHENTICATE = "MEMBER_AUTHENTICATE";
	private static final String MEMBER_AUTHENTICATIONS = "MEMBER_AUTHENTICATIONS";
	private static final String MEMBER_MEMBERSHIPS = "MEMBER_MEMBERSHIPS";
	private static final String MEMBER_ARTICLE_ONE = "MEMBER_ARTICLE_ONE";
	private static final String MEMBER_ARTICLE_TWO = "MEMBER_ARTICLE_TWO";
	private static final String MEMBER_ARTICLE_THREE = "MEMBER_ARTICLE_THREE";
	private static final String MEMBER_LISTING_ONE = "MEMBER_LISTING_ONE";
	private static final String MEMBER_LISTING_TWO = "MEMBER_LISTING_TWO";
	private static final String MEMBER_LISTING_THREE = "MEMBER_LISTING_THREE";
	private static final String MEMBER_OFFER_ONE = "MEMBER_OFFER_ONE";
	private static final String MEMBER_OFFER_TWO = "MEMBER_OFFER_TWO";

	private static final String OWNER_AUTHENTICATE = "OWNER_AUTHENTICATE";
	private static final String OWNER_AUTHENTICATIONS = "OWNER_AUTHENTICATIONS";
	private static final String OWNER_ARTICLE_ONE = "OWNER_ARTICLE_ONE";
	private static final String OWNER_ARTICLE_TWO = "OWNER_ARTICLE_TWO";
	private static final String OWNER_LISTING_ONE = "OWNER_LISTING_ONE";
	private static final String OWNER_LISTING_TWO = "OWNER_LISTING_TWO";

	private static final String OWNER_TRADES_POST = "OWNER_TRADES_POST";
	private static final String OWNER_MEMBERSHIP = "OWNER_MEMBERSHIP"; 
	private static final String OWNER_OFFER_ONE = "OWNER_OFFER_ONE";
	
	private static final String TRADE_MATCHING_ARTICLES = "TRADE_MATCHING_ARTICLES"; 
	private static final String TRADE_GENERATE_TRADES = "TRADE_GENERATE_TRADES"; 
	private static final String TRADE_RESULTS = "TRADE_RESULTS";

	@Override
	public String contentFilePath() {
		return "tutorial.html";
	}

	@Override
	public String content() {
		String template = TemplateHelper.buildTemplate(contentFilePath());

		Header ownerAuthorizationHeader = MatchAndTradeRestUtil.getLastAuthorizationHeader();
		MatchAndTradeApiFacade ownerApiFacade = new MatchAndTradeApiFacade(ownerAuthorizationHeader);

		// Building a user with a given user name for documentation clarity
		UserJson ownerUser = MatchAndTradeRestUtil.getLastAuthenticatedUser();
		ownerUser.setName("Olavo");
		ownerApiFacade.saveUser(ownerUser);

		// OWNER_AUTHENTICATE
		SpecificationParser ownerAuthenticateParser = AuthenticateRestDocMaker.buildGetParser();
		template = TemplateHelper.replacePlaceholder(template, OWNER_AUTHENTICATE, ownerAuthenticateParser.asHtmlSnippet());

		// OWNER_AUTHENTICATIONS
		SpecificationParser ownerAuthenticationsParser = AuthenticationRestDocMaker.buildGetParser();
		template = TemplateHelper.replacePlaceholder(template, OWNER_AUTHENTICATIONS, ownerAuthenticationsParser.asHtmlSnippet());

		// OWNER_TRADES_POST
		SpecificationParser ownerTradeParser = TradeRestDocMaker.buildPostParser(ownerAuthorizationHeader);
		template = TemplateHelper.replacePlaceholder(template, OWNER_TRADES_POST, ownerTradeParser.asHtmlSnippet());
		TradeJson trade = ownerTradeParser.getResponse().as(TradeJson.class);

		//OWNER_MEMBERSHIP
		SpecificationParser ownerMembershipParser = MembershipRestDocMaker.buildSearchMembershipParser(ownerUser.getUserId(), trade.getTradeId(), ownerAuthorizationHeader);
		template = TemplateHelper.replacePlaceholder(template, OWNER_MEMBERSHIP, ownerMembershipParser.asHtmlSnippet());
		MembershipJson ownerMembership = ListingRestDocMaker.buildMembership(ownerAuthorizationHeader, trade);

		// OWNER_ARTICLE_ONE
		ArticleJson ownerPandemicOneArticle = new ArticleJson();
		ownerPandemicOneArticle.setName("Pandemic Legacy: Season 1");
		SpecificationParser ownerPandemicOneParser = ArticleRestDocMaker.buildPostParser(ownerPandemicOneArticle);
		template = TemplateHelper.replacePlaceholder(template, OWNER_ARTICLE_ONE, ownerPandemicOneParser.asHtmlSnippet());
		ownerPandemicOneArticle = ownerPandemicOneParser.getResponse().as(ArticleJson.class);

		// OWNER_ARTICLE_TWO
		ArticleJson ownerPandemicTwoArticle = new ArticleJson();
		ownerPandemicTwoArticle.setName("Pandemic Legacy: Season 2");
		SpecificationParser ownerPandemicTwoParser = ArticleRestDocMaker.buildPostParser(ownerPandemicTwoArticle);
		template = TemplateHelper.replacePlaceholder(template, OWNER_ARTICLE_TWO, ownerPandemicTwoParser.asHtmlSnippet());

		// OWNER_LISTING_ONE
		SpecificationParser ownerPandemicOneListingParser = ListingRestDocMaker.buildPostListingParser(
				ownerAuthorizationHeader,
				ownerMembership,
				ownerPandemicOneArticle);
		template = TemplateHelper.replacePlaceholder(template, OWNER_LISTING_ONE, ownerPandemicOneListingParser.asHtmlSnippet());

		SpecificationParser ownerPandemicTwoListingParser = ListingRestDocMaker.buildPostListingParser(
				ownerAuthorizationHeader,
				ownerMembership,
				ownerPandemicTwoArticle);
		template = TemplateHelper.replacePlaceholder(template, OWNER_LISTING_TWO, ownerPandemicTwoListingParser.asHtmlSnippet());


		// MEMBER SETUP
		Header memberAuthorizationHeader = MatchAndTradeRestUtil.nextAuthorizationHeader();
		MatchAndTradeApiFacade memberApiFacade = new MatchAndTradeApiFacade(memberAuthorizationHeader);

		// Building a user with a given user name for documentation clarity
		UserJson memberUser = MatchAndTradeRestUtil.getLastAuthenticatedUser();
		memberUser.setName("Maria");
		memberApiFacade.saveUser(memberUser);

		// MEMBER_AUTHENTICATE
		SpecificationParser memberAuthenticateParser = AuthenticateRestDocMaker.buildGetParser();
		template = TemplateHelper.replacePlaceholder(template, MEMBER_AUTHENTICATE, memberAuthenticateParser.asHtmlSnippet());

		// MEMBER_AUTHENTICATIONS
		SpecificationParser memberAuthenticationsParser = AuthenticationRestDocMaker.buildGetParser();
		template = TemplateHelper.replacePlaceholder(template, MEMBER_AUTHENTICATIONS, memberAuthenticationsParser.asHtmlSnippet());
		memberAuthenticationsParser.getResponse().as(AuthenticationJson.class);

		//MEMBER_MEMBERSHIP
		MembershipJson memberMembership = new MembershipJson();
		memberMembership.setUserId(memberUser.getUserId());
		memberMembership.setTradeId(trade.getTradeId());
		SpecificationParser memberMembershipParser = MembershipRestDocMaker.parserPostMembership(memberMembership);
		template = TemplateHelper.replacePlaceholder(template, MEMBER_MEMBERSHIPS, memberMembershipParser.asHtmlSnippet());
		memberMembership = memberMembershipParser.getResponse().as(MembershipJson.class);

		// MEMBER_ARTICLE_ONE
		ArticleJson memberStoneAgeArticle = new ArticleJson();
		memberStoneAgeArticle.setName("Stone Age");
		SpecificationParser memberStoneAgeParser = ArticleRestDocMaker.buildPostParser(memberStoneAgeArticle);
		template = TemplateHelper.replacePlaceholder(template, MEMBER_ARTICLE_ONE, memberStoneAgeParser.asHtmlSnippet());
		memberStoneAgeArticle = memberStoneAgeParser.getResponse().as(ArticleJson.class);

		// MEMBER_ARTICLE_TWO
		ArticleJson memberCarcassoneArticle = new ArticleJson();
		memberCarcassoneArticle.setName("Carcassonne");
		SpecificationParser memberCarcasssoneParser = ArticleRestDocMaker.buildPostParser(memberCarcassoneArticle);
		template = TemplateHelper.replacePlaceholder(template, MEMBER_ARTICLE_TWO, memberStoneAgeParser.asHtmlSnippet());
		memberCarcassoneArticle = memberCarcasssoneParser.getResponse().as(ArticleJson.class);

		// MEMBER_ARTICLE_THREE
		ArticleJson memberNoThanksArticle = new ArticleJson();
		memberNoThanksArticle.setName("No Thanks!");
		SpecificationParser memberNoThanksParser = ArticleRestDocMaker.buildPostParser(memberNoThanksArticle);
		template = TemplateHelper.replacePlaceholder(template, MEMBER_ARTICLE_THREE, memberNoThanksParser.asHtmlSnippet());
		memberNoThanksArticle = memberNoThanksParser.getResponse().as(ArticleJson.class);

		// MEMBER_LISTING_ONE
		SpecificationParser memberStoneAgeListing = ListingRestDocMaker.buildPostListingParser(
				memberAuthorizationHeader,
				memberMembership,
				memberStoneAgeArticle);
		template = TemplateHelper.replacePlaceholder(template,
				MEMBER_LISTING_ONE,
				memberStoneAgeListing.asHtmlSnippet());
		// MEMBER_LISTING_ONE
		SpecificationParser memberCarcassonneListing = ListingRestDocMaker.buildPostListingParser(
				memberAuthorizationHeader,
				memberMembership,
				memberCarcassoneArticle);
		template = TemplateHelper.replacePlaceholder(template,
				MEMBER_LISTING_TWO,
				memberCarcassonneListing.asHtmlSnippet());
		// MEMBER_LISTING_ONE
		SpecificationParser memberNoThanksListingParser = ListingRestDocMaker.buildPostListingParser(
				memberAuthorizationHeader,
				memberMembership,
				memberNoThanksArticle);
		template = TemplateHelper.replacePlaceholder(template,
				MEMBER_LISTING_THREE,
				memberNoThanksListingParser.asHtmlSnippet());

		// TRADE_MATCHING_ARTICLES
		trade.setState(TradeJson.State.MATCHING_ARTICLES);
		SpecificationParser tradeMatchArticlesParser = TradeRestDocMaker.buildPutParser(ownerAuthorizationHeader, trade);
		template = TemplateHelper.replacePlaceholder(template, TRADE_MATCHING_ARTICLES, tradeMatchArticlesParser.asHtmlSnippet());

		// OWNER_OFFER_ONE
		SpecificationParser ownerPandemicOneForStoneAgeOffer = OfferRestDocMaker.parsePostOffer(
				ownerAuthorizationHeader,
				ownerMembership,
				ownerPandemicOneArticle.getArticleId(),
				memberStoneAgeArticle.getArticleId());
		template = TemplateHelper.replacePlaceholder(template,
				OWNER_OFFER_ONE,
				ownerPandemicOneForStoneAgeOffer.asHtmlSnippet());

		// MEMBER_OFFER_ONE
		SpecificationParser memberStoneAgeForPandemicOneArticle = OfferRestDocMaker.parsePostOffer(
				memberAuthorizationHeader,
				memberMembership,
				memberStoneAgeArticle.getArticleId(),
				ownerPandemicOneArticle.getArticleId());
		template = TemplateHelper.replacePlaceholder(template,
				MEMBER_OFFER_ONE,
				memberStoneAgeForPandemicOneArticle.asHtmlSnippet());

		// MEMBER_OFFER_ONE
		SpecificationParser memberStoneAgeForPandemicTwoArticle = OfferRestDocMaker.parsePostOffer(
				memberAuthorizationHeader,
				memberMembership,
				memberStoneAgeArticle.getArticleId(),
				ownerPandemicTwoArticle.getArticleId());
		template = TemplateHelper.replacePlaceholder(template,
				MEMBER_OFFER_TWO,
				memberStoneAgeForPandemicTwoArticle.asHtmlSnippet());

		// TRADE_GENERATE_TRADES
		trade.setState(TradeJson.State.GENERATE_RESULTS);
		SpecificationParser tradeGenerateResultsParser = TradeRestDocMaker.buildPutParser(ownerAuthorizationHeader, trade);
		template = TemplateHelper.replacePlaceholder(template, TRADE_GENERATE_TRADES, tradeGenerateResultsParser.asHtmlSnippet());

		// TRADE_RESULTS
		SpecificationParser tradeResultParser = TradeResultRestDocMaker.parseCsvResults(trade, ownerAuthorizationHeader);
		template = TemplateHelper.replacePlaceholder(template, TRADE_RESULTS, tradeResultParser.asHtmlSnippet());

		return TemplateHelper.appendHeaderAndFooter(template);
	}
}
