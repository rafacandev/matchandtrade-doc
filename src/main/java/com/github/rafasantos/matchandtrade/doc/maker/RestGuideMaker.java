package com.github.rafasantos.matchandtrade.doc.maker;

import org.apache.http.Header;

import com.github.rafasantos.matchandtrade.doc.maker.rest.RestAuthenticateMaker;
import com.github.rafasantos.matchandtrade.doc.maker.rest.RestAuthenticationsMaker;
import com.github.rafasantos.matchandtrade.doc.maker.rest.RestTradeMembershipsMaker;
import com.github.rafasantos.matchandtrade.doc.maker.rest.RestTradesMaker;
import com.github.rafasantos.matchandtrade.doc.util.JsonUtil;
import com.github.rafasantos.matchandtrade.doc.util.RequestResponseHolder;
import com.github.rafasantos.matchandtrade.doc.util.RestUtil;
import com.github.rafasantos.matchandtrade.doc.util.SnippetUtil;
import com.github.rafasantos.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;
import com.matchandtrade.rest.v1.json.UserJson;

public class RestGuideMaker implements OutputMaker {
	
	private static final String AUTHENTICATE_SNIPPET_SECOND = "AUTHENTICATE_SNIPPET_SECOND";
	private static final String AUTHENTICATIONS_SNIPPET_SECOND = "AUTHENTICATIONS_SNIPPET_SECOND";
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		
		// Assemble authentication snippet for the first user
		RestAuthenticateMaker fUserAuthenticate = new RestAuthenticateMaker();
		RequestResponseHolder fUserAuthenticateRRH = fUserAuthenticate.buildAuthenticateRequestResponse();
		String fUserAuthenticateSnippet = TemplateUtil.buildSnippet(fUserAuthenticateRRH.getHttpRequest(), fUserAuthenticateRRH.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, RestAuthenticateMaker.AUTHENTICATE_SNIPPET, fUserAuthenticateSnippet);

		// Assign a new Authorization header
		Header firstUserAuthorizationHeader = fUserAuthenticateRRH.getHttpResponse().getHeaders("Authorization")[0];
		RestUtil.setAuthenticationHeader(firstUserAuthorizationHeader);
		
		// Assemble Trade snippet		
		TradeJson tradeJson = new TradeJson();
		tradeJson.setName("Trading board-games in Toronto.");
		RequestResponseHolder requestResponseTrades = SnippetUtil.buildPostRequestResponse(RestTradesMaker.BASE_URL, tradeJson);
		String tradesPostSnippet = TemplateUtil.buildSnippet(requestResponseTrades.getHttpRequest(), requestResponseTrades.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, RestTradesMaker.TRADES_POST_SNIPPET, tradesPostSnippet);
		tradeJson = JsonUtil.fromHttpResponse(requestResponseTrades.getHttpResponse(), TradeJson.class);
		
		// Assemble authentication snippet for a second user
		RequestResponseHolder sUserAuthenticateRRH = fUserAuthenticate.buildAuthenticateRequestResponse();
		String sUserAuthenticateSecond = TemplateUtil.buildSnippet(sUserAuthenticateRRH.getHttpRequest(), sUserAuthenticateRRH.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATE_SNIPPET_SECOND, sUserAuthenticateSecond);

		// Assign a new Authorization header
		Header secondUserAuthorizationHeader = sUserAuthenticateRRH.getHttpResponse().getHeaders("Authorization")[0];
		RestUtil.setAuthenticationHeader(secondUserAuthorizationHeader);

		// Assemble authentications snippet for the second user		
		RestAuthenticationsMaker sUserAuthentication = new RestAuthenticationsMaker();
		RequestResponseHolder sUserAuthenticationsRRH = sUserAuthentication.buildGetAuthenticationsRequestResponse();
		String sUserAuthenticationSnippet = TemplateUtil.buildSnippet(sUserAuthenticationsRRH.getHttpRequest(), sUserAuthenticationsRRH.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATIONS_SNIPPET_SECOND, sUserAuthenticationSnippet);
		UserJson userJsonSecond = JsonUtil.fromHttpResponse(sUserAuthenticationsRRH.getHttpResponse(), UserJson.class);
		
		// Assemble Trade Membership		
		TradeMembershipJson tradeMembershipJson = new TradeMembershipJson();
		tradeMembershipJson.setTradeId(tradeJson.getTradeId());
		tradeMembershipJson.setUserId(userJsonSecond.getUserId());;
		RequestResponseHolder requestResponseTradeMemberhips = SnippetUtil.buildPostRequestResponse(RestTradeMembershipsMaker.BASE_URL, tradeMembershipJson);
		String tradeMemberhipsPostSnippet = TemplateUtil.buildSnippet(requestResponseTradeMemberhips.getHttpRequest(), requestResponseTradeMemberhips.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, RestTradeMembershipsMaker.TRADES_MEMBERSHIP_POST_SNIPPET, tradeMemberhipsPostSnippet);

		return template;
	}
	
	@Override
	public String getDocLocation() {
		return "doc/rest-guide.md";
	}
}
