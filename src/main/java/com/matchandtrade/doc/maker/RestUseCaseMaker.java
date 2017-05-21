package com.matchandtrade.doc.maker;

import com.matchandtrade.doc.maker.rest.RestAuthenticateMaker;
import com.matchandtrade.doc.maker.rest.RestAuthenticationMaker;
import com.matchandtrade.doc.maker.rest.RestTradeMaker;
import com.matchandtrade.doc.maker.rest.RestTradeMembershipMaker;
import com.matchandtrade.doc.util.JsonUtil;
import com.matchandtrade.doc.util.RequestResponseHolder;
import com.matchandtrade.doc.util.RequestResponseUtil;
import com.matchandtrade.doc.util.RestUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;
import com.matchandtrade.rest.v1.json.UserJson;

public class RestUseCaseMaker implements OutputMaker {
	
	private static final String AUTHENTICATE_SNIPPET_SECOND = "AUTHENTICATE_SNIPPET_SECOND";
	private static final String AUTHENTICATIONS_SNIPPET_SECOND = "AUTHENTICATIONS_SNIPPET_SECOND";
	
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
		String firatAuthenticationsSnippet = TemplateUtil.buildSnippet(firstAuthentication.getHttpRequest(), firstAuthentication.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, RestAuthenticationMaker.AUTHENTICATIONS_SNIPPET, firatAuthenticationsSnippet);

		// TRADES_POST_SNIPPET
		TradeJson tradeJson = new TradeJson();
		tradeJson.setName("Board games in Ottawa");
		RequestResponseHolder requestResponseTrade = RequestResponseUtil.buildPostRequestResponse(RestTradeMaker.BASE_URL, tradeJson);
		String tradesPostSnippet = TemplateUtil.buildSnippet(requestResponseTrade.getHttpRequest(), requestResponseTrade.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, RestTradeMaker.TRADES_POST_SNIPPET, tradesPostSnippet);
		tradeJson = JsonUtil.fromHttpResponse(requestResponseTrade.getHttpResponse(), TradeJson.class);

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

		return template;
	}
	
	@Override
	public String getDocLocation() {
		return "doc/rest-use-cases.md";
	}
}
