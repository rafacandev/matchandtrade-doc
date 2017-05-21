package com.github.rafasantos.matchandtrade.doc.maker.rest;

import com.github.rafasantos.matchandtrade.doc.maker.OutputMaker;
import com.matchandtrade.doc.util.JsonUtil;
import com.matchandtrade.doc.util.RequestResponseHolder;
import com.matchandtrade.doc.util.SnippetUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.ItemJson;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;


public class RestItemMaker implements OutputMaker {
	
	public static final String BASE_URL = "/items";
	public static final String ITEMS_POST_SNIPPET = "ITEMS_POST_SNIPPET";
	public static final String ITEMS_PUT_SNIPPET = "ITEMS_PUT_SNIPPET";
	public static final String ITEMS_GET_SNIPPET = "ITEMS_GET_SNIPPET";
	public static final String ITEMS_SEARCH_SNIPPET = "ITEMS_SEARCH_SNIPPET";
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
		
		// PUT rest/v1/trade-memberships/{tradeMembershipId}/items/
		Integer itemId = postJson.getItemId();
		postJson.setItemId(null); // Set as null because we do not want the id to be displayed on the request body to emphasize that the id must be sent on the URL
		postJson.setName(postJson.getName() + " After PUT");
		RequestResponseHolder put = SnippetUtil.buildPutRequestResponse(RestTradeMembershipsMaker.BASE_URL + tradeMembership.getTradeMembershipId() + BASE_URL + "/" + itemId , postJson);
		String putSnippet = TemplateUtil.buildSnippet(put.getHttpRequest(), put.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, ITEMS_PUT_SNIPPET, putSnippet);

		// GET rest/v1/trade-memberships/{tradeMembershipId}/items/{itemId}
		RequestResponseHolder get = SnippetUtil.buildGetRequestResponse(RestTradeMembershipsMaker.BASE_URL + tradeMembership.getTradeMembershipId() + BASE_URL + "/" + itemId);
		String getSnippet = TemplateUtil.buildSnippet(get.getHttpRequest(), get.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, ITEMS_GET_SNIPPET, getSnippet);

		// GET rest/v1/trade-memberships/{tradeMembershipId}/items/
		RequestResponseHolder getAll = SnippetUtil.buildGetRequestResponse(RestTradeMembershipsMaker.BASE_URL + tradeMembership.getTradeMembershipId() + BASE_URL);
		String getAllSnippet = TemplateUtil.buildSnippet(getAll.getHttpRequest(), getAll.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, ITEMS_GET_ALL_SNIPPET, getAllSnippet);

		// GET rest/v1/trade-memberships/{tradeMembershipId}/items?name=
		RequestResponseHolder search = SnippetUtil.buildGetRequestResponse(RestTradeMembershipsMaker.BASE_URL + tradeMembership.getTradeMembershipId() + BASE_URL + "?name=Pandemic%20Legacy:%20Season%201%20After%20PUT");
		String searchSnippet = TemplateUtil.buildSnippet(search.getHttpRequest(), search.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, ITEMS_SEARCH_SNIPPET, searchSnippet);
		
		return template;
	}

	@Override
	public String getDocLocation() {
		return "doc/rest/items.md";
	}

}
