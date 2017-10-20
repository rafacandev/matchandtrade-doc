package com.matchandtrade.doc.maker;

import java.util.List;
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
import com.matchandtrade.rest.Json;
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
	private static final String MEMBER_ITEM_ONE_SNIPPET = "MEMBER_ITEM_ONE_SNIPPET";
	private static final String MEMBER_ITEM_TWO_SNIPPET = "MEMBER_ITEM_TWO_SNIPPET";
	private static final String MEMBER_ITEM_THREE_SNIPPET = "MEMBER_ITEM_THREE_SNIPPET";
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
	
	private String template = "";
	
	
	@Override
	public String buildDocContent() {
		template = TemplateUtil.buildTemplate(getDocLocation());
		
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
		tradeJson = handlePost(RestTradeMaker.BASE_URL + "/", tradeJson, OWNER_TRADES_POST_SNIPPET);
		@SuppressWarnings("unchecked")
		List<Map<Object,Object>> x = (List<Map<Object, Object>>) handleGet(
				RestTradeMembershipMaker.BASE_URL + "?tradeId=" + tradeJson.getTradeId() + "&userId=" + firstAuthenticationJson.getUserId()
				, OWNER_TRADE_MEMBERSHIP_SNIPPET
				, List.class);
		Integer ownerTradeMembershipId = (Integer) x.get(0).get("tradeMembershipId");
		
		// OWNER_ITEM_ONE_SNIPPET
		ItemJson ownerItemOneJson = new ItemJson();
		ownerItemOneJson.setName("Pandemic Legacy: Season 1");
		ownerItemOneJson = handlePost(
				RestTradeMembershipMaker.BASE_URL + ownerTradeMembershipId + RestItemMaker.BASE_URL
				, ownerItemOneJson
				, OWNER_ITEM_ONE_SNIPPET);

		// OWNER_ITEM_TWO_SNIPPET
		ItemJson ownerItemTwoJson = new ItemJson();
		ownerItemTwoJson.setName("Pandemic Legacy: Season 2");
		ownerItemTwoJson = handlePost(
				RestTradeMembershipMaker.BASE_URL + ownerTradeMembershipId + RestItemMaker.BASE_URL
				, ownerItemTwoJson
				, OWNER_ITEM_TWO_SNIPPET);
		
		// MEMBER_AUTHENTICATE_SNIPPET
		RequestResponseHolder memberAuthenticate = RequestResponseUtil.buildAuthenticateRequestResponse();
		RestUtil.setAuthenticationHeader(memberAuthenticate.getAuthorizationHeader());
		String memberAuthenticateSnippet = TemplateUtil.buildSnippet(memberAuthenticate.getHttpRequest(), memberAuthenticate.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, MEMBER_AUTHENTICATE_SNIPPET, memberAuthenticateSnippet);

		// MEMBER_AUTHENTICATIONS_SNIPPET		
		RequestResponseHolder memberAuthentication = RequestResponseUtil.buildGetRequestResponse(RestAuthenticationMaker.BASE_URL);
		String memberAuthenticationsSnippet = TemplateUtil.buildSnippet(memberAuthentication.getHttpRequest(), memberAuthentication.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, MEMBER_AUTHENTICATIONS_SNIPPET, memberAuthenticationsSnippet);
		UserJson memberUserJson = RestUtil.getAuthenticatedUser();
		
		// MEMBER_TRADES_MEMBERSHIP_SNIPPET	
		TradeMembershipJson memberTradeMembershipJson = new TradeMembershipJson();
		memberTradeMembershipJson.setTradeId(tradeJson.getTradeId());
		memberTradeMembershipJson.setUserId(memberUserJson.getUserId());;
		memberTradeMembershipJson = handlePost(RestTradeMembershipMaker.BASE_URL
				, memberTradeMembershipJson
				, MEMBER_TRADES_MEMBERSHIP_SNIPPET);
		
		// MEMBER_ITEM_ONE
		ItemJson memberItemOneJson = new ItemJson();
		memberItemOneJson.setName("Stone Age");
		memberItemOneJson = handlePost(
				RestTradeMembershipMaker.BASE_URL + memberTradeMembershipJson.getTradeMembershipId() + RestItemMaker.BASE_URL
				, memberItemOneJson
				, MEMBER_ITEM_ONE_SNIPPET);

		// MEMBER_ITEM_TWO
		ItemJson memberItemTwoJson = new ItemJson();
		memberItemTwoJson.setName("Carcassonne");
		memberItemTwoJson = handlePost(
				RestTradeMembershipMaker.BASE_URL + memberTradeMembershipJson.getTradeMembershipId() + RestItemMaker.BASE_URL
				, memberItemTwoJson
				, MEMBER_ITEM_TWO_SNIPPET);

		// MEMBER_ITEM_TWO
		ItemJson memberItemThreeJson = new ItemJson();
		memberItemThreeJson.setName("No Thanks!");
		memberItemThreeJson = handlePost(
				RestTradeMembershipMaker.BASE_URL + memberTradeMembershipJson.getTradeMembershipId() + RestItemMaker.BASE_URL
				, memberItemThreeJson
				, MEMBER_ITEM_THREE_SNIPPET);
		
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
		ownerWantItem.setItemId(memberItemOneJson.getItemId());
		handlePost(
				RestTradeMembershipMaker.BASE_URL + ownerTradeMembershipId + RestItemMaker.BASE_URL + "/" + ownerItemOneJson.getItemId() + RestWantItemMaker.BASE_URL,
				ownerWantItem
				, OWNER_WANT_ITEMS_ONE);
		
		// MEMBER_WANT_ITEMS_ONE
		RestUtil.setAuthenticationHeader(memberAuthenticate.getAuthorizationHeader());
		WantItemJson memberWantItem = new WantItemJson();
		memberWantItem.setPriority(0);
		memberWantItem.setItemId(ownerItemOneJson.getItemId());
		handlePost(
				RestTradeMembershipMaker.BASE_URL + memberTradeMembershipJson.getTradeMembershipId() + RestItemMaker.BASE_URL + "/" + memberItemOneJson.getItemId() + RestWantItemMaker.BASE_URL
				, memberWantItem
				, MEMBER_WANT_ITEMS_ONE);
		
		// MEMBER_WANT_ITEMS_TWO
		WantItemJson memberWantItem2 = new WantItemJson();
		memberWantItem2.setPriority(1);
		memberWantItem2.setItemId(ownerItemTwoJson.getItemId());
		handlePost(
				RestTradeMembershipMaker.BASE_URL + memberTradeMembershipJson.getTradeMembershipId() + RestItemMaker.BASE_URL + "/" + memberItemOneJson.getItemId() + RestWantItemMaker.BASE_URL
				, memberWantItem2
				, MEMBER_WANT_ITEMS_TWO);
		
		// TRADE_MATCHING_ITEMS_ENDED
		RestUtil.setAuthenticationHeader(firstAuthenticate.getAuthorizationHeader());
		tradeJson.setState(TradeJson.State.MATCHING_ITEMS_ENDED);
		tradeJson.setTradeId(null); // We do not want tradeId displayed in the documentation
		RequestResponseHolder tradeMatchingEnded = RequestResponseUtil.buildPutRequestResponse(RestTradeMaker.BASE_URL + "/" + tradeId, tradeJson);
		String tradeMatchingEndedSnippet = TemplateUtil.buildSnippet(tradeMatchingEnded.getHttpRequest(), tradeMatchingEnded.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADE_MATCHING_ITEMS_ENDED, tradeMatchingEndedSnippet);
		
		// TRADE_RESULTS
		handleGet(RestTradeMaker.BASE_URL + "/" + tradeId + "/results", TRADE_RESULTS);
		
		return template;
	}

	@SuppressWarnings("unchecked")
	private <T extends Json> T handlePost(String url, T requestBody, String placeHolder) {
		RequestResponseHolder requestResponseHolder = RequestResponseUtil.buildPostRequestResponse(url, requestBody);
		String snippet = TemplateUtil.buildSnippet(requestResponseHolder.getHttpRequest(), requestResponseHolder.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, placeHolder, snippet);
		return (T) JsonUtil.fromHttpResponse(requestResponseHolder.getHttpResponse(), requestBody.getClass());
	}

	private <T> T handleGet(String url, String placeHolder, Class<T> clazz) {
		return JsonUtil.fromString(handleGet(url, placeHolder), clazz);
	}

	private String handleGet(String url, String placeHolder) {
		RequestResponseHolder requestResponseHolder = RequestResponseUtil.buildGetRequestResponse(url);
		RestUtil.buildResponseBodyString(requestResponseHolder.getHttpResponse());
		String snippet = TemplateUtil.buildSnippet(requestResponseHolder.getHttpRequest(), requestResponseHolder.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, placeHolder, snippet);
		return RestUtil.buildResponseBodyString(requestResponseHolder.getHttpResponse());
	}
	
	@Override
	public String getDocLocation() {
		return "use-cases.html";
	}
}
