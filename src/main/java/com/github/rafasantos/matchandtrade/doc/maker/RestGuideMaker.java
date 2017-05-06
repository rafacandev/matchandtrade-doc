package com.github.rafasantos.matchandtrade.doc.maker;

import org.apache.http.Header;

import com.github.rafasantos.matchandtrade.doc.maker.rest.RestAuthenticateMaker;
import com.github.rafasantos.matchandtrade.doc.maker.rest.RestAuthenticationsMaker;
import com.github.rafasantos.matchandtrade.doc.maker.rest.RestTradesMaker;
import com.github.rafasantos.matchandtrade.doc.util.RequestResponseHolder;
import com.github.rafasantos.matchandtrade.doc.util.RestUtil;
import com.github.rafasantos.matchandtrade.doc.util.SnippetUtil;
import com.github.rafasantos.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.TradeJson;

public class RestGuideMaker implements OutputMaker {
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		
		// Assemble authentication snippet
		RestAuthenticateMaker authenticate = new RestAuthenticateMaker();
		RequestResponseHolder requestResponseAuth = authenticate.buildAuthenticateRequestResponse();
		String authenticateSnippet = TemplateUtil.buildSnippet(requestResponseAuth.getHttpRequest(), requestResponseAuth.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, RestAuthenticateMaker.AUTHENTICATE_SNIPPET, authenticateSnippet);

		// Use the same authorization header for documentation clarity and consistency
		Header authorizationHeader = requestResponseAuth.getHttpResponse().getHeaders("Authorization")[0];
		// Reuse the same Authorization header for better documentation clarity
		RestUtil.setAuthenticationHeader(authorizationHeader);
		
		// Assemble authentications snippet		
		RestAuthenticationsMaker authentications = new RestAuthenticationsMaker();
		RequestResponseHolder requestResponseAuthentications = authentications.buildGetAuthenticationsRequestResponse();
		String authenticationsSnippet = TemplateUtil.buildSnippet(requestResponseAuthentications.getHttpRequest(), requestResponseAuthentications.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, RestAuthenticationsMaker.AUTHENTICATIONS_SNIPPET, authenticationsSnippet);

		// Assemble Trade snippet		
		TradeJson tradeJson = new TradeJson();
		tradeJson.setName("Trading video-games in New York.");
		RequestResponseHolder requestResponseTrades = SnippetUtil.buildPostRequestResponse("/rest/v1/trades/", tradeJson);
		String tradesSnippet = TemplateUtil.buildSnippet(requestResponseTrades.getHttpRequest(), requestResponseTrades.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, RestTradesMaker.TRADES_POST_SNIPPET, tradesSnippet);
		
		return template;
	}
	
	@Override
	public String getDocLocation() {
		return "doc/rest-guide.md";
	}
}
