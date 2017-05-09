package com.github.rafasantos.matchandtrade.doc.maker.rest;

import com.github.rafasantos.matchandtrade.doc.maker.OutputMaker;
import com.github.rafasantos.matchandtrade.doc.util.JsonUtil;
import com.github.rafasantos.matchandtrade.doc.util.RequestResponseHolder;
import com.github.rafasantos.matchandtrade.doc.util.SnippetUtil;
import com.github.rafasantos.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.ItemJson;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;


public class RestItemMaker implements OutputMaker {
	
	public static final String BASE_URL = "/items";
	public static final String ITEMS_POST_SNIPPET = "ITEMS_POST_SNIPPET";
	public static final String ITEMS_GET_SNIPPET = "ITEMS_GET_SNIPPET";
	public static final String ITEMS_GET_ALL_SNIPPET = "ITEMS_GET_ALL_SNIPPET";
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		
		// Create a trade membership
		RequestResponseHolder tradeMembershipRRH = RestTradeMembershipsMaker.buildPostJson("Board games in Montreal");
		TradeMembershipJson tradeMembership = JsonUtil.fromHttpResponse(tradeMembershipRRH.getHttpResponse(), TradeMembershipJson.class);

		// POST rest/v1/trade-memberships/{tradeMembershipId}/items/
		ItemJson postJson = new ItemJson();
		postJson.setName("Pandemic Legacy: Season 1");
		RequestResponseHolder post = SnippetUtil.buildPostRequestResponse(RestTradeMembershipsMaker.BASE_URL + tradeMembership.getTradeMembershipId() + BASE_URL, postJson);
		String postSnippet = TemplateUtil.buildSnippet(post.getHttpRequest(), post.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, ITEMS_POST_SNIPPET, postSnippet);
		postJson = JsonUtil.fromHttpResponse(post.getHttpResponse(), ItemJson.class);

		// GET rest/v1/trade-memberships/{tradeMembershipId}/items/{itemId}
		RequestResponseHolder get = SnippetUtil.buildGetRequestResponse(RestTradeMembershipsMaker.BASE_URL + tradeMembership.getTradeMembershipId() + BASE_URL + "/" + postJson.getItemId());
		String getSnippet = TemplateUtil.buildSnippet(get.getHttpRequest(), get.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, ITEMS_GET_SNIPPET, getSnippet);

		// GET rest/v1/trade-memberships/{tradeMembershipId}/items/
		RequestResponseHolder getAll = SnippetUtil.buildGetRequestResponse(RestTradeMembershipsMaker.BASE_URL + tradeMembership.getTradeMembershipId() + BASE_URL);
		String getAllSnippet = TemplateUtil.buildSnippet(getAll.getHttpRequest(), getAll.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, ITEMS_GET_ALL_SNIPPET, getAllSnippet);
		
		return template;
	}

	@Override
	public String getDocLocation() {
		return "doc/rest/items.md";
	}

}
