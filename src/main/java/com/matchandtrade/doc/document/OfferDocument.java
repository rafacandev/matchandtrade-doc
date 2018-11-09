package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.clientapi.MatchAndTradeClient;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.*;

public class OfferDocument implements Document {
	
	private static final String OFFERS_POST = "OFFERS_POST";
	private static final String OFFERS_GET = "OFFERS_GET";
	private static final String OFFERS_SEARCH = "OFFERS_SEARCH";
	private static final String OFFERS_DELETE = "OFFERS_DELETE";

	private final MatchAndTradeClient memberClientApi;
	private final MatchAndTradeClient ownerClientApi;
	private String template;

	public OfferDocument() {
		ownerClientApi = new MatchAndTradeClient();
		memberClientApi = new MatchAndTradeClient();
		template = TemplateUtil.buildTemplate(contentFilePath());
	}

	@Override
	public String content() {
		// ### Setup a trade with an owner and a member so the can later make offers for their articles
		// Create a user named 'Olavo', this is the trade owner
		UserJson ownerUser = ownerClientApi.findUser().getResponse().as(UserJson.class);
		ownerUser.setName("Olavo");
		ownerClientApi.update(ownerUser);

		// Olavo creates a trade
		TradeJson trade = createTrade();
		MembershipJson ownerMembership = ownerClientApi.findMembershipByUserIdAndTradeIdAsMembership(ownerUser.getUserId(), trade.getTradeId());

		// Create another user named 'Maria', this will become a trade member
		UserJson memberUser = memberClientApi.findUser().getResponse().as(UserJson.class);
		memberUser.setName("Maria");
		memberClientApi.update(memberUser);

		// Maria subscribes to Olavo's trade
		MembershipJson memberMembership = new MembershipJson();
		memberMembership.setUserId(memberUser.getUserId());
		memberMembership.setTradeId(trade.getTradeId());
		memberMembership = memberClientApi.create(memberMembership).getResponse().as(MembershipJson.class);

		// List articles
		ArticleJson ownerArticle = new ArticleJson();
		ownerArticle.setName("Pandemic Legacy: Season 1");
		ownerArticle = ownerClientApi.create(ownerArticle).getResponse().as(ArticleJson.class);
		listArticle(ownerMembership, ownerArticle, ownerClientApi);
		ArticleJson memberArticle = new ArticleJson();
		memberArticle.setName("Stone Age");
		memberArticle = memberClientApi.create(memberArticle).getResponse().as(ArticleJson.class);
		listArticle(memberMembership, memberArticle, memberClientApi);
		// End of setup

		// OFFERS_POST
		OfferJson offer = new OfferJson();
		offer.setOfferedArticleId(ownerArticle.getArticleId());
		offer.setWantedArticleId(memberArticle.getArticleId());
		SpecificationParser postOfferParser = ownerClientApi.create(ownerMembership.getMembershipId(), offer);
		template = TemplateUtil.replacePlaceholder(template, OFFERS_POST, postOfferParser.asHtmlSnippet());
		offer = postOfferParser.getResponse().body().as(OfferJson.class);

		// OFFERS_GET
		SpecificationParser getOfferParser = ownerClientApi.findOffer(ownerMembership.getMembershipId(), offer.getOfferId());
		template = TemplateUtil.replacePlaceholder(template, OFFERS_GET, getOfferParser.asHtmlSnippet());

		// OFFERS_SEARCH
		SpecificationParser searchOffersParser = ownerClientApi.findOffersByMembershipIdAndOfferedArticleIdAndWantedArticleId(ownerMembership.getMembershipId(), ownerArticle.getArticleId(), memberArticle.getArticleId());
		template = TemplateUtil.replacePlaceholder(template, OFFERS_SEARCH, searchOffersParser.asHtmlSnippet());

		// OFFERS_DELETE
		SpecificationParser deleteOfferParser = ownerClientApi.deleteOffer(ownerMembership.getMembershipId(), offer.getOfferId());
		template = TemplateUtil.replacePlaceholder(template, OFFERS_DELETE, deleteOfferParser.asHtmlSnippet());

		template = TemplateUtil.replacePaginationRows(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

	@Override
	public String contentFilePath() {
		return "offers.html";
	}

	private TradeJson createTrade() {
		TradeJson trade = new TradeJson();
		trade.setName("Board games in Brasilia - " + System.currentTimeMillis());
		trade = ownerClientApi.create(trade).getResponse().as(TradeJson.class);
		return trade;
	}

	private void listArticle(MembershipJson ownerMembership, ArticleJson ownerPandemic, MatchAndTradeClient clientApi) {
		ListingJson listing = new ListingJson();
		listing.setMembershipId(ownerMembership.getMembershipId());
		listing.setArticleId(ownerPandemic.getArticleId());
		clientApi.create(listing);
	}

}
