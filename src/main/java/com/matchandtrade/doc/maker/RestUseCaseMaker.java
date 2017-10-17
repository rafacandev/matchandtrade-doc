package com.matchandtrade.doc.maker;

import java.util.Map;

import com.matchandtrade.doc.maker.rest.RestAuthenticationMaker;
import com.matchandtrade.doc.maker.rest.RestItemMaker;
import com.matchandtrade.doc.maker.rest.RestTradeMaker;
import com.matchandtrade.doc.maker.rest.RestTradeMembershipMaker;
import com.matchandtrade.doc.maker.rest.RestWantItemMaker;
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
import com.matchandtrade.rest.v1.json.WantItemJson;

public class RestUseCaseMaker extends OutputMaker {
	
	private static final String MEMBER_AUTHENTICATE_SNIPPET = "MEMBER_AUTHENTICATE_SNIPPET";
	private static final String MEMBER_AUTHENTICATIONS_SNIPPET = "MEMBER_AUTHENTICATIONS_SNIPPET";
	private static final String MEMBER_TRADES_MEMBERSHIP_SNIPPET = "MEMBER_TRADES_MEMBERSHIP_SNIPPET";
	private static final String MEMBER_ITEM_ONE = "MEMBER_ITEM_ONE";
	private static final String MEMBER_ITEM_TWO = "MEMBER_ITEM_TWO";
	private static final String MEMBER_ITEM_THREE = "MEMBER_ITEM_THREE";
	private static final String MEMBER_WANT_ITEMS_ONE = "MEMBER_WANT_ITEMS_ONE";
	private static final String MEMBER_WANT_ITEMS_TWO = "MEMBER_WANT_ITEMS_TWO";
	
	private static final String OWNER_AUTHENTICATE_SNIPPET = "OWNER_AUTHENTICATE_SNIPPET";
	private static final String OWNER_AUTHENTICATIONS_SNIPPET = "OWNER_AUTHENTICATIONS_SNIPPET";
	private static final String OWNER_ITEM_ONE_SNIPPET = "OWNER_ITEM_ONE_SNIPPET";
	private static final String OWNER_ITEM_TWO_SNIPPET = "OWNER_ITEM_TWO_SNIPPET";
	private static final String OWNER_TRADES_POST_SNIPPET = "OWNER_TRADES_POST_SNIPPET";
	private static final String OWNER_TRADE_MEMBERSHIP_SNIPPET = "OWNER_TRADE_MEMBERSHIP_SNIPPET"; 
	private static final String OWNER_WANT_ITEMS_ONE = "OWNER_WANT_ITEMS_ONE";
	
	private static final String TRADE_MATCHING_ITEMS_SNIPPET = "TRADE_MATCHING_ITEMS_SNIPPET"; 
	private static final String TRADE_MATCHING_ITEMS_ENDED = "TRADE_MATCHING_ITEMS_ENDED"; 
	private static final String TRADE_RESULTS = "TRADE_RESULTS";
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		
		// OWNER_AUTHENTICATE_SNIPPET
		RequestResponseHolder firstAuthenticate = RequestResponseUtil.buildAuthenticateRequestResponse();
		RestUtil.setAuthenticationHeader(firstAuthenticate.getAuthorizationHeader());
		String firstAuthenticateSnippet = TemplateUtil.buildSnippet(firstAuthenticate.getHttpRequest(), firstAuthenticate.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, OWNER_AUTHENTICATE_SNIPPET, firstAuthenticateSnippet);

		// OWNER_AUTHENTICATIONS_SNIPPET
		RequestResponseHolder firstAuthentication = RequestResponseUtil.buildGetRequestResponse(RestAuthenticationMaker.BASE_URL);
		String firstAuthenticationsSnippet = TemplateUtil.buildSnippet(firstAuthentication.getHttpRequest(), firstAuthentication.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, OWNER_AUTHENTICATIONS_SNIPPET, firstAuthenticationsSnippet);
		AuthenticationJson firstAuthenticationJson = JsonUtil.fromHttpResponse(firstAuthentication.getHttpResponse(), AuthenticationJson.class);

		// OWNER_TRADES_POST_SNIPPET
		TradeJson tradeJson = new TradeJson();
		tradeJson.setName("Board games in Ottawa");
		RequestResponseHolder trade = RequestResponseUtil.buildPostRequestResponse(RestTradeMaker.BASE_URL + "/", tradeJson);
		String tradesSnippet = TemplateUtil.buildSnippet(trade.getHttpRequest(), trade.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, OWNER_TRADES_POST_SNIPPET, tradesSnippet);
		tradeJson = JsonUtil.fromHttpResponse(trade.getHttpResponse(), TradeJson.class);
		
		// OWNER_TRADE_MEMBERSHIP_SNIPPET
		RequestResponseHolder ownerTradeMembership = RequestResponseUtil.buildGetRequestResponse(RestTradeMembershipMaker.BASE_URL + "?tradeId=" + tradeJson.getTradeId() + "&userId=" + firstAuthenticationJson.getUserId());
		String ownerTradeMembershipSnippet = TemplateUtil.buildSnippet(ownerTradeMembership.getHttpRequest(), ownerTradeMembership.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, OWNER_TRADE_MEMBERSHIP_SNIPPET, ownerTradeMembershipSnippet);
		String ownerResponseString = RestUtil.buildResponseBodyString(ownerTradeMembership.getHttpResponse());
		// TODO, find a better approach for getting the tradeMembeshipId
		@SuppressWarnings("unchecked")
		Map<Object,Object> ownerResponseMap = (Map<Object, Object>) JsonUtil.fromStringToMap(ownerResponseString).get(0);
		Integer ownerTradeMembershipId = (Integer) ownerResponseMap.get("tradeMembershipId");
		
		// OWNER_ITEM_ONE_SNIPPET
		ItemJson itemOneJson = new ItemJson();
		itemOneJson.setName("Pandemic Legacy: Season 1");
		RequestResponseHolder itemOne = RequestResponseUtil.buildPostRequestResponse(RestTradeMembershipMaker.BASE_URL + ownerTradeMembershipId + RestItemMaker.BASE_URL, itemOneJson);
		String itemOneSnippet = TemplateUtil.buildSnippet(itemOne.getHttpRequest(), itemOne.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, OWNER_ITEM_ONE_SNIPPET, itemOneSnippet);
		itemOneJson = JsonUtil.fromHttpResponse(itemOne.getHttpResponse(), ItemJson.class);

		// OWNER_ITEM_TWO_SNIPPET
		ItemJson itemTwoJson = new ItemJson();
		itemTwoJson.setName("Pandemic Legacy: Season 2");
		RequestResponseHolder itemTwo = RequestResponseUtil.buildPostRequestResponse(RestTradeMembershipMaker.BASE_URL + ownerTradeMembershipId + RestItemMaker.BASE_URL, itemTwoJson);
		String itemTwoSnippet = TemplateUtil.buildSnippet(itemTwo.getHttpRequest(), itemTwo.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, OWNER_ITEM_TWO_SNIPPET, itemTwoSnippet);
		itemTwoJson = JsonUtil.fromHttpResponse(itemTwo.getHttpResponse(), ItemJson.class);
		
		// MEMBER_AUTHENTICATE_SNIPPET
		RequestResponseHolder secondAuthenticate = RequestResponseUtil.buildAuthenticateRequestResponse();
		RestUtil.setAuthenticationHeader(secondAuthenticate.getAuthorizationHeader());
		String secondAuthenticateSnippet = TemplateUtil.buildSnippet(secondAuthenticate.getHttpRequest(), secondAuthenticate.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, MEMBER_AUTHENTICATE_SNIPPET, secondAuthenticateSnippet);

		// MEMBER_AUTHENTICATIONS_SNIPPET		
		RequestResponseHolder secondAuthentication = RequestResponseUtil.buildGetRequestResponse(RestAuthenticationMaker.BASE_URL);
		String secondAuthenticationsSnippet = TemplateUtil.buildSnippet(secondAuthentication.getHttpRequest(), secondAuthentication.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, MEMBER_AUTHENTICATIONS_SNIPPET, secondAuthenticationsSnippet);
		UserJson secondUserJson = RestUtil.getAuthenticatedUser();
		
		// MEMBER_TRADES_MEMBERSHIP_SNIPPET	
		TradeMembershipJson tradeMembershipJson = new TradeMembershipJson();
		tradeMembershipJson.setTradeId(tradeJson.getTradeId());
		tradeMembershipJson.setUserId(secondUserJson.getUserId());;
		RequestResponseHolder tradeMemberhip = RequestResponseUtil.buildPostRequestResponse(RestTradeMembershipMaker.BASE_URL, tradeMembershipJson);
		String tradeMemberhipSnippet = TemplateUtil.buildSnippet(tradeMemberhip.getHttpRequest(), tradeMemberhip.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, MEMBER_TRADES_MEMBERSHIP_SNIPPET, tradeMemberhipSnippet);
		tradeMembershipJson = JsonUtil.fromHttpResponse(tradeMemberhip.getHttpResponse(), TradeMembershipJson.class);

		// MEMBER_ITEM_ONE
		ItemJson itemThreeJson = new ItemJson();
		itemThreeJson.setName("Stone Age");
		RequestResponseHolder itemThree = RequestResponseUtil.buildPostRequestResponse(RestTradeMembershipMaker.BASE_URL + tradeMembershipJson.getTradeMembershipId() + RestItemMaker.BASE_URL, itemThreeJson);
		String itemThreeSnippet = TemplateUtil.buildSnippet(itemThree.getHttpRequest(), itemThree.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, MEMBER_ITEM_ONE, itemThreeSnippet);
		itemThreeJson = JsonUtil.fromHttpResponse(itemThree.getHttpResponse(), ItemJson.class);

		// MEMBER_ITEM_TWO
		ItemJson itemFourJson = new ItemJson();
		itemFourJson.setName("Carcassonne");
		RequestResponseHolder itemFour = RequestResponseUtil.buildPostRequestResponse(RestTradeMembershipMaker.BASE_URL + tradeMembershipJson.getTradeMembershipId() + RestItemMaker.BASE_URL, itemFourJson);
		String itemFourSnippet = TemplateUtil.buildSnippet(itemFour.getHttpRequest(), itemFour.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, MEMBER_ITEM_TWO, itemFourSnippet);
		itemFourJson = JsonUtil.fromHttpResponse(itemFour.getHttpResponse(), ItemJson.class);

		// MEMBER_ITEM_TWO
		ItemJson itemFiveJson = new ItemJson();
		itemFiveJson.setName("No Thanks!");
		RequestResponseHolder itemFive = RequestResponseUtil.buildPostRequestResponse(RestTradeMembershipMaker.BASE_URL + tradeMembershipJson.getTradeMembershipId() + RestItemMaker.BASE_URL, itemFiveJson);
		String itemFiveSnippet = TemplateUtil.buildSnippet(itemFive.getHttpRequest(), itemFive.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, MEMBER_ITEM_THREE, itemFiveSnippet);
		itemFiveJson = JsonUtil.fromHttpResponse(itemFive.getHttpResponse(), ItemJson.class);

		// TRADE_MATCHING_ITEMS_SNIPPET
		RestUtil.setAuthenticationHeader(firstAuthenticate.getAuthorizationHeader());
		tradeJson.setState(TradeJson.State.MATCHING_ITEMS);
		Integer tradeId = tradeJson.getTradeId();
		tradeJson.setTradeId(null); // We do not want tradeId displayed in the documentation
		RequestResponseHolder tradeMatching = RequestResponseUtil.buildPutRequestResponse(RestTradeMaker.BASE_URL + "/" + tradeId, tradeJson);
		String tradeMatchingSnippet = TemplateUtil.buildSnippet(tradeMatching.getHttpRequest(), tradeMatching.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADE_MATCHING_ITEMS_SNIPPET, tradeMatchingSnippet);
		
		// OWNER_WANT_ITEMS_ONE
		WantItemJson ownerWantItem = new WantItemJson();
		ownerWantItem.setPriority(0);
		ownerWantItem.setItemId(itemThreeJson.getItemId());
		String ownerWantItemUrl = RestTradeMembershipMaker.BASE_URL + ownerTradeMembershipId + RestItemMaker.BASE_URL + "/" + itemOneJson.getItemId() + RestWantItemMaker.BASE_URL ; 
		RequestResponseHolder ownerWantItemRRH = RequestResponseUtil.buildPostRequestResponse(ownerWantItemUrl, ownerWantItem);
		String ownerWantItemSnippet = TemplateUtil.buildSnippet(ownerWantItemRRH.getHttpRequest(), ownerWantItemRRH.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, OWNER_WANT_ITEMS_ONE, ownerWantItemSnippet);

		// MEMBER_WANT_ITEMS_ONE
		RestUtil.setAuthenticationHeader(secondAuthenticate.getAuthorizationHeader());
		WantItemJson memberWantItem = new WantItemJson();
		memberWantItem.setPriority(0);
		memberWantItem.setItemId(itemOneJson.getItemId());
		String memberWantItemUrl = RestTradeMembershipMaker.BASE_URL + tradeMembershipJson.getTradeMembershipId() + RestItemMaker.BASE_URL + "/" + itemThreeJson.getItemId() + RestWantItemMaker.BASE_URL ; 
		RequestResponseHolder memberWantItemRRH = RequestResponseUtil.buildPostRequestResponse(memberWantItemUrl, memberWantItem);
		String memberWantItemSnippet = TemplateUtil.buildSnippet(memberWantItemRRH.getHttpRequest(), memberWantItemRRH.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, MEMBER_WANT_ITEMS_ONE, memberWantItemSnippet);

		// MEMBER_WANT_ITEMS_TWO
		WantItemJson memberWantItem2 = new WantItemJson();
		memberWantItem2.setPriority(1);
		memberWantItem2.setItemId(itemTwoJson.getItemId());
		String memberWantItem2Url = RestTradeMembershipMaker.BASE_URL + tradeMembershipJson.getTradeMembershipId() + RestItemMaker.BASE_URL + "/" + itemThreeJson.getItemId() + RestWantItemMaker.BASE_URL ; 
		RequestResponseHolder memberWantItem2RRH = RequestResponseUtil.buildPostRequestResponse(memberWantItem2Url, memberWantItem2);
		String memberWantItem2Snippet = TemplateUtil.buildSnippet(memberWantItem2RRH.getHttpRequest(), memberWantItem2RRH.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, MEMBER_WANT_ITEMS_TWO, memberWantItem2Snippet);
		
		// TRADE_MATCHING_ITEMS_ENDED
		RestUtil.setAuthenticationHeader(firstAuthenticate.getAuthorizationHeader());
		tradeJson.setState(TradeJson.State.MATCHING_ITEMS_ENDED);
		tradeJson.setTradeId(null); // We do not want tradeId displayed in the documentation
		RequestResponseHolder tradeMatchingEnded = RequestResponseUtil.buildPutRequestResponse(RestTradeMaker.BASE_URL + "/" + tradeId, tradeJson);
		String tradeMatchingEndedSnippet = TemplateUtil.buildSnippet(tradeMatchingEnded.getHttpRequest(), tradeMatchingEnded.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADE_MATCHING_ITEMS_ENDED, tradeMatchingEndedSnippet);
		
		// TRADE_RESULTS
		String tradeResultURL = RestTradeMaker.BASE_URL + "/" + tradeId + "/results";
		RequestResponseHolder tradeResultRRH = RequestResponseUtil.buildGetRequestResponse(tradeResultURL);
		String tradeResultSnippet = TemplateUtil.buildSnippet(tradeResultRRH.getHttpRequest(), tradeResultRRH.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADE_RESULTS, tradeResultSnippet);
		
		return template;
	}
	
	@Override
	public String getDocLocation() {
		return "use-cases.html";
	}
}
