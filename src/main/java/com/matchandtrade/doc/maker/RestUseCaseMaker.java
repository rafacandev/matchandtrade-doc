package com.matchandtrade.doc.maker;

import java.util.Map;

import com.matchandtrade.doc.maker.rest.RestAuthenticateMaker;
import com.matchandtrade.doc.maker.rest.RestAuthenticationMaker;
import com.matchandtrade.doc.maker.rest.RestItemMaker;
import com.matchandtrade.doc.maker.rest.RestTradeMaker;
import com.matchandtrade.doc.maker.rest.RestTradeMembershipMaker;
import com.matchandtrade.doc.util.JsonUtil;
import com.matchandtrade.doc.util.RequestResponseHolder;
import com.matchandtrade.doc.util.RequestResponseUtil;
import com.matchandtrade.doc.util.RestUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.AuthenticationJson;
import com.matchandtrade.rest.v1.json.ItemJson;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;
import com.matchandtrade.rest.v1.json.UserJson;

public class RestUseCaseMaker implements OutputMaker {
	
	private static final String AUTHENTICATE_SNIPPET_SECOND = "AUTHENTICATE_SNIPPET_SECOND";
	private static final String AUTHENTICATIONS_SNIPPET_SECOND = "AUTHENTICATIONS_SNIPPET_SECOND";
	private static final String ITEM_ONE = "ITEM_ONE";
	private static final String ITEM_TWO = "ITEM_TWO";
	private static final String ITEM_THREE = "ITEM_THREE";
	private static final String ITEM_FOUR = "ITEM_FOUR";
	private static final String ITEM_FIVE = "ITEM_FIVE";
	private static final String TRADE_MEMBERSHIP_OWNER = "TRADE_MEMBERSHIP_OWNER"; 
	private static final String TRADE_MATCHING_ITEMS_SNIPPET = "TRADE_MATCHING_ITEMS_SNIPPET"; 
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		
		// AUTHENTICATE_SNIPPET
		RequestResponseHolder firstAuthenticate = RequestResponseUtil.buildAuthenticateRequestResponse();
		RestUtil.setAuthenticationHeader(firstAuthenticate.getAuthorizationHeader());
		String firstAuthenticateSnippet = TemplateUtil.buildSnippet(firstAuthenticate.getHttpRequest(), firstAuthenticate.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, RestAuthenticateMaker.AUTHENTICATE_SNIPPET, firstAuthenticateSnippet);

		// AUTHENTICATIONS_SNIPPET
		RequestResponseHolder firstAuthentication = RequestResponseUtil.buildGetRequestResponse(RestAuthenticationMaker.BASE_URL);
		String firstAuthenticationsSnippet = TemplateUtil.buildSnippet(firstAuthentication.getHttpRequest(), firstAuthentication.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, RestAuthenticationMaker.AUTHENTICATIONS_SNIPPET, firstAuthenticationsSnippet);
		AuthenticationJson firstAuthenticationJson = JsonUtil.fromHttpResponse(firstAuthentication.getHttpResponse(), AuthenticationJson.class);

		// TRADES_POST_SNIPPET
		TradeJson tradeJson = new TradeJson();
		tradeJson.setName("Board games in Ottawa");
		RequestResponseHolder trade = RequestResponseUtil.buildPostRequestResponse(RestTradeMaker.BASE_URL + "/", tradeJson);
		String tradesSnippet = TemplateUtil.buildSnippet(trade.getHttpRequest(), trade.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, RestTradeMaker.TRADES_POST_SNIPPET, tradesSnippet);
		tradeJson = JsonUtil.fromHttpResponse(trade.getHttpResponse(), TradeJson.class);
		
		// TRADE_MEMBERSHIP_OWNER
		RequestResponseHolder ownerTradeMembership = RequestResponseUtil.buildGetRequestResponse(RestTradeMembershipMaker.BASE_URL + "?tradeId=" + tradeJson.getTradeId() + "&userId=" + firstAuthenticationJson.getUserId());
		String ownerTradeMembershipSnippet = TemplateUtil.buildSnippet(ownerTradeMembership.getHttpRequest(), ownerTradeMembership.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADE_MEMBERSHIP_OWNER, ownerTradeMembershipSnippet);
		String ownerResponseString = RestUtil.buildResponseBodyString(ownerTradeMembership.getHttpResponse());
		// TODO, find a better approach for getting the tradeMembeshipId
		@SuppressWarnings("unchecked")
		Map<Object,Object> ownerResponseMap = (Map<Object, Object>) JsonUtil.fromStringToMap(ownerResponseString).get(0);
		Integer ownerTradeMembershipId = (Integer) ownerResponseMap.get("tradeMembershipId");
		
		// ITEM_ONE
		ItemJson itemOneJson = new ItemJson();
		itemOneJson.setName("Pandemic Legacy: Season 1");
		RequestResponseHolder itemOne = RequestResponseUtil.buildPostRequestResponse(RestTradeMembershipMaker.BASE_URL + ownerTradeMembershipId + RestItemMaker.BASE_URL, itemOneJson);
		String itemOneSnippet = TemplateUtil.buildSnippet(itemOne.getHttpRequest(), itemOne.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, ITEM_ONE, itemOneSnippet);
		itemOneJson = JsonUtil.fromHttpResponse(itemOne.getHttpResponse(), ItemJson.class);

		// ITEM_TWO
		ItemJson itemTwoJson = new ItemJson();
		itemTwoJson.setName("Pandemic Legacy: Season 2");
		RequestResponseHolder itemTwo = RequestResponseUtil.buildPostRequestResponse(RestTradeMembershipMaker.BASE_URL + ownerTradeMembershipId + RestItemMaker.BASE_URL, itemTwoJson);
		String itemTwoSnippet = TemplateUtil.buildSnippet(itemTwo.getHttpRequest(), itemTwo.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, ITEM_TWO, itemTwoSnippet);
		itemTwoJson = JsonUtil.fromHttpResponse(itemTwo.getHttpResponse(), ItemJson.class);
		

		// AUTHENTICATE_SNIPPET_SECOND
		RequestResponseHolder secondAuthenticate = RequestResponseUtil.buildAuthenticateRequestResponse();
		RestUtil.setAuthenticationHeader(secondAuthenticate.getAuthorizationHeader());
		String secondAuthenticateSnippet = TemplateUtil.buildSnippet(secondAuthenticate.getHttpRequest(), secondAuthenticate.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATE_SNIPPET_SECOND, secondAuthenticateSnippet);

		// AUTHENTICATIONS_SNIPPET_SECOND		
		RequestResponseHolder secondAuthentication = RequestResponseUtil.buildGetRequestResponse(RestAuthenticationMaker.BASE_URL);
		String secondAuthenticationsSnippet = TemplateUtil.buildSnippet(secondAuthentication.getHttpRequest(), secondAuthentication.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATIONS_SNIPPET_SECOND, secondAuthenticationsSnippet);
		UserJson secondUserJson = RestUtil.getAuthenticatedUser();
		
		// TRADES_MEMBERSHIP_POST_SNIPPET	
		TradeMembershipJson tradeMembershipJson = new TradeMembershipJson();
		tradeMembershipJson.setTradeId(tradeJson.getTradeId());
		tradeMembershipJson.setUserId(secondUserJson.getUserId());;
		RequestResponseHolder tradeMemberhip = RequestResponseUtil.buildPostRequestResponse(RestTradeMembershipMaker.BASE_URL, tradeMembershipJson);
		String tradeMemberhipSnippet = TemplateUtil.buildSnippet(tradeMemberhip.getHttpRequest(), tradeMemberhip.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, RestTradeMembershipMaker.TRADES_MEMBERSHIP_POST_SNIPPET, tradeMemberhipSnippet);
		tradeMembershipJson = JsonUtil.fromHttpResponse(tradeMemberhip.getHttpResponse(), TradeMembershipJson.class);

		// ITEM_THREE
		ItemJson itemThreeJson = new ItemJson();
		itemThreeJson.setName("No Thanks!");
		RequestResponseHolder itemThree = RequestResponseUtil.buildPostRequestResponse(RestTradeMembershipMaker.BASE_URL + tradeMembershipJson.getTradeMembershipId() + RestItemMaker.BASE_URL, itemThreeJson);
		String itemThreeSnippet = TemplateUtil.buildSnippet(itemThree.getHttpRequest(), itemThree.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, ITEM_THREE, itemThreeSnippet);
		itemThreeJson = JsonUtil.fromHttpResponse(itemThree.getHttpResponse(), ItemJson.class);

		// ITEM_FOUR
		ItemJson itemFourJson = new ItemJson();
		itemFourJson.setName("Carcassonne");
		RequestResponseHolder itemFour = RequestResponseUtil.buildPostRequestResponse(RestTradeMembershipMaker.BASE_URL + tradeMembershipJson.getTradeMembershipId() + RestItemMaker.BASE_URL, itemFourJson);
		String itemFourSnippet = TemplateUtil.buildSnippet(itemFour.getHttpRequest(), itemFour.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, ITEM_FOUR, itemFourSnippet);
		itemFourJson = JsonUtil.fromHttpResponse(itemFour.getHttpResponse(), ItemJson.class);

		// ITEM_FOUR
		ItemJson itemFiveJson = new ItemJson();
		itemFiveJson.setName("Stone Age");
		RequestResponseHolder itemFive = RequestResponseUtil.buildPostRequestResponse(RestTradeMembershipMaker.BASE_URL + tradeMembershipJson.getTradeMembershipId() + RestItemMaker.BASE_URL, itemFiveJson);
		String itemFiveSnippet = TemplateUtil.buildSnippet(itemFive.getHttpRequest(), itemFive.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, ITEM_FIVE, itemFiveSnippet);
		itemFiveJson = JsonUtil.fromHttpResponse(itemFive.getHttpResponse(), ItemJson.class);

		// TRADES_MATCHING_ITEMS_SNIPPET
		RestUtil.setAuthenticationHeader(firstAuthenticate.getAuthorizationHeader());
		tradeJson.setState(TradeJson.State.MATCHING_ITEMS);
		RequestResponseHolder tradeMatching = RequestResponseUtil.buildPutRequestResponse(RestTradeMaker.BASE_URL + "/" + tradeJson.getTradeId(), tradeJson);
		String tradeMatchingSnippet = TemplateUtil.buildSnippet(tradeMatching.getHttpRequest(), tradeMatching.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADE_MATCHING_ITEMS_SNIPPET, tradeMatchingSnippet);
		
		return template;
	}
	
	@Override
	public String getDocLocation() {
		return "doc/rest-use-cases.md";
	}
}
