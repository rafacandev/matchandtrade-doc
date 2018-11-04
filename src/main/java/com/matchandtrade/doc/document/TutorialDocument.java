package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeClient;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.*;
import io.restassured.http.Header;

public class TutorialDocument implements Document {
	
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

	private String template;
	private final MatchAndTradeClient ownerClientApi;
	private Header memberAuthorizationHeader;
	private MatchAndTradeClient memberClientApi;
	private final Header ownerAuthorizationHeader;

	public TutorialDocument() {
		ownerAuthorizationHeader = MatchAndTradeRestUtil.getLastAuthorizationHeader();
		ownerClientApi = new MatchAndTradeClient(ownerAuthorizationHeader);

		memberAuthorizationHeader = MatchAndTradeRestUtil.nextAuthorizationHeader();
		memberClientApi = new MatchAndTradeClient(memberAuthorizationHeader);

		template = TemplateUtil.buildTemplate(contentFilePath());
	}


	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());

		MatchAndTradeApiFacade ownerApiFacade = new MatchAndTradeApiFacade(ownerAuthorizationHeader);

		// Building a user with a given user name for documentation clarity
		UserJson ownerUser = ownerClientApi.findUser().getResponse().as(UserJson.class);
		ownerUser.setName("Olavo");
		ownerApiFacade.saveUser(ownerUser);

		// OWNER_AUTHENTICATE
		SpecificationParser ownerAuthenticateParser = MatchAndTradeClient.authenticate();
		template = TemplateUtil.replacePlaceholder(template, OWNER_AUTHENTICATE, ownerAuthenticateParser.asHtmlSnippet());

		// OWNER_AUTHENTICATIONS
		SpecificationParser ownerAuthenticationsParser = ownerClientApi.findAuthentications();
		template = TemplateUtil.replacePlaceholder(template, OWNER_AUTHENTICATIONS, ownerAuthenticationsParser.asHtmlSnippet());

		// OWNER_TRADES_POST
		TradeJson trade = new TradeJson();
		trade.setName("Board games in Toronto - " + System.currentTimeMillis());
		SpecificationParser ownerTradeParser = ownerClientApi.create(trade);
		template = TemplateUtil.replacePlaceholder(template, OWNER_TRADES_POST, ownerTradeParser.asHtmlSnippet());
		trade = ownerTradeParser.getResponse().as(TradeJson.class);

		//OWNER_MEMBERSHIP
		SpecificationParser ownerMembershipParser = ownerClientApi.findMembershipByUserIdOrTradeId(ownerUser.getUserId(), trade.getTradeId());
		template = TemplateUtil.replacePlaceholder(template, OWNER_MEMBERSHIP, ownerMembershipParser.asHtmlSnippet());
		MembershipJson ownerMembership = ownerClientApi.findMembershipByUserIdOrTradeIdAsMembership(ownerUser.getUserId(), trade.getTradeId());

		// OWNER_ARTICLE_ONE
		ArticleJson ownerPandemicOneArticle = new ArticleJson();
		ownerPandemicOneArticle.setName("Pandemic Legacy: Season 1");
		SpecificationParser ownerPandemicOneParser = ownerClientApi.create(ownerPandemicOneArticle);
		template = TemplateUtil.replacePlaceholder(template, OWNER_ARTICLE_ONE, ownerPandemicOneParser.asHtmlSnippet());
		ownerPandemicOneArticle = ownerPandemicOneParser.getResponse().as(ArticleJson.class);

		// OWNER_ARTICLE_TWO
		ArticleJson ownerPandemicTwoArticle = new ArticleJson();
		ownerPandemicTwoArticle.setName("Pandemic Legacy: Season 2");
		SpecificationParser ownerPandemicTwoParser = ownerClientApi.create(ownerPandemicTwoArticle);
		template = TemplateUtil.replacePlaceholder(template, OWNER_ARTICLE_TWO, ownerPandemicTwoParser.asHtmlSnippet());
		ownerPandemicTwoArticle = ownerPandemicTwoParser.getResponse().as(ArticleJson.class);

		// OWNER_LISTING_ONE
		SpecificationParser ownerPandemicOneListingParser = ownerClientApi.create(buildListing(ownerMembership, ownerPandemicOneArticle));
		template = TemplateUtil.replacePlaceholder(template, OWNER_LISTING_ONE, ownerPandemicOneListingParser.asHtmlSnippet());

		// OWNER_LISTING_TWO
		SpecificationParser ownerPandemicTwoListingParser = ownerClientApi.create(buildListing(ownerMembership, ownerPandemicTwoArticle));
		template = TemplateUtil.replacePlaceholder(template, OWNER_LISTING_TWO, ownerPandemicTwoListingParser.asHtmlSnippet());

		// MEMBER SETUP
		MatchAndTradeApiFacade memberApiFacade = new MatchAndTradeApiFacade(memberAuthorizationHeader);

		// Building a user with a given user name for documentation clarity
		UserJson memberUser = memberClientApi.findUser().getResponse().as(UserJson.class);
		memberUser.setName("Maria");
		memberApiFacade.saveUser(memberUser);

		// MEMBER_AUTHENTICATE
		SpecificationParser memberAuthenticateParser = MatchAndTradeClient.authenticate();
		template = TemplateUtil.replacePlaceholder(template, MEMBER_AUTHENTICATE, memberAuthenticateParser.asHtmlSnippet());

		// MEMBER_AUTHENTICATIONS
		SpecificationParser memberAuthenticationsParser = memberClientApi.findAuthentications();
		template = TemplateUtil.replacePlaceholder(template, MEMBER_AUTHENTICATIONS, memberAuthenticationsParser.asHtmlSnippet());
		memberAuthenticationsParser.getResponse().as(AuthenticationJson.class);

		//MEMBER_MEMBERSHIP
		MembershipJson memberMembership = new MembershipJson();
		memberMembership.setUserId(memberUser.getUserId());
		memberMembership.setTradeId(trade.getTradeId());
		SpecificationParser memberMembershipParser = memberClientApi.create(memberMembership);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_MEMBERSHIPS, memberMembershipParser.asHtmlSnippet());
		memberMembership = memberMembershipParser.getResponse().as(MembershipJson.class);

		// MEMBER_ARTICLE_ONE
		ArticleJson memberStoneAgeArticle = new ArticleJson();
		memberStoneAgeArticle.setName("Stone Age");
		SpecificationParser memberStoneAgeParser = memberClientApi.create(memberStoneAgeArticle);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_ARTICLE_ONE, memberStoneAgeParser.asHtmlSnippet());
		memberStoneAgeArticle = memberStoneAgeParser.getResponse().as(ArticleJson.class);

		// MEMBER_ARTICLE_TWO
		ArticleJson memberCarcassoneArticle = new ArticleJson();
		memberCarcassoneArticle.setName("Carcassonne");
		SpecificationParser memberCarcasssoneParser = memberClientApi.create(memberCarcassoneArticle);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_ARTICLE_TWO, memberStoneAgeParser.asHtmlSnippet());
		memberCarcassoneArticle = memberCarcasssoneParser.getResponse().as(ArticleJson.class);

		// MEMBER_ARTICLE_THREE
		ArticleJson memberNoThanksArticle = new ArticleJson();
		memberNoThanksArticle.setName("No Thanks!");
		SpecificationParser memberNoThanksParser = memberClientApi.create(memberNoThanksArticle);
		template = TemplateUtil.replacePlaceholder(template, MEMBER_ARTICLE_THREE, memberNoThanksParser.asHtmlSnippet());
		memberNoThanksArticle = memberNoThanksParser.getResponse().as(ArticleJson.class);

		// MEMBER_LISTING_ONE
		SpecificationParser memberStoneAgeListing = memberClientApi.create(buildListing(memberMembership, memberStoneAgeArticle));
		template = TemplateUtil.replacePlaceholder(template,
				MEMBER_LISTING_ONE,
				memberStoneAgeListing.asHtmlSnippet());
		// MEMBER_LISTING_ONE
		SpecificationParser memberCarcassonneListing = memberClientApi.create(buildListing(memberMembership, memberCarcassoneArticle));
		template = TemplateUtil.replacePlaceholder(template,
				MEMBER_LISTING_TWO,
				memberCarcassonneListing.asHtmlSnippet());
		// MEMBER_LISTING_ONE
		SpecificationParser memberNoThanksListingParser = memberClientApi.create(buildListing(memberMembership, memberNoThanksArticle));
		template = TemplateUtil.replacePlaceholder(template,
				MEMBER_LISTING_THREE,
				memberNoThanksListingParser.asHtmlSnippet());

		// TRADE_MATCHING_ARTICLES
		trade.setState(TradeJson.State.MATCHING_ARTICLES);
		SpecificationParser tradeMatchArticlesParser = ownerClientApi.update(trade);
		template = TemplateUtil.replacePlaceholder(template, TRADE_MATCHING_ARTICLES, tradeMatchArticlesParser.asHtmlSnippet());

		// OWNER_OFFER_ONE
		SpecificationParser ownerPandemicOneForStoneAgeOffer = OfferDocument.parsePostOffer(
				ownerAuthorizationHeader,
				ownerMembership,
				ownerPandemicOneArticle.getArticleId(),
				memberStoneAgeArticle.getArticleId());
		template = TemplateUtil.replacePlaceholder(template,
				OWNER_OFFER_ONE,
				ownerPandemicOneForStoneAgeOffer.asHtmlSnippet());

		// MEMBER_OFFER_ONE
		SpecificationParser memberStoneAgeForPandemicOneArticle = OfferDocument.parsePostOffer(
				memberAuthorizationHeader,
				memberMembership,
				memberStoneAgeArticle.getArticleId(),
				ownerPandemicOneArticle.getArticleId());
		template = TemplateUtil.replacePlaceholder(template,
				MEMBER_OFFER_ONE,
				memberStoneAgeForPandemicOneArticle.asHtmlSnippet());

		// MEMBER_OFFER_ONE
		SpecificationParser memberStoneAgeForPandemicTwoArticle = OfferDocument.parsePostOffer(
				memberAuthorizationHeader,
				memberMembership,
				memberStoneAgeArticle.getArticleId(),
				ownerPandemicTwoArticle.getArticleId());
		template = TemplateUtil.replacePlaceholder(template,
				MEMBER_OFFER_TWO,
				memberStoneAgeForPandemicTwoArticle.asHtmlSnippet());

		// TRADE_GENERATE_TRADES
		trade.setState(TradeJson.State.GENERATE_RESULTS);
		SpecificationParser tradeGenerateResultsParser = ownerClientApi.update(trade);
		template = TemplateUtil.replacePlaceholder(template, TRADE_GENERATE_TRADES, tradeGenerateResultsParser.asHtmlSnippet());

		// TRADE_RESULTS
		SpecificationParser tradeResultParser = TradeResultDocument.parseCsvResults(trade, ownerAuthorizationHeader);
		template = TemplateUtil.replacePlaceholder(template, TRADE_RESULTS, tradeResultParser.asHtmlSnippet());

		return TemplateUtil.appendHeaderAndFooter(template);
	}

	private ListingJson buildListing(MembershipJson ownerMembership, ArticleJson ownerPandemicOneArticle) {
		ListingJson result = new ListingJson();
		result.setMembershipId(ownerMembership.getMembershipId());
		result.setArticleId(ownerPandemicOneArticle.getArticleId());
		return result;
	}
}
