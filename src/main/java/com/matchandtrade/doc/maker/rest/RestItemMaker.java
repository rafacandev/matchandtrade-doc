package com.matchandtrade.doc.maker.rest;

import com.matchandtrade.doc.maker.OutputMaker;
import com.matchandtrade.doc.util.JsonUtil;
import com.matchandtrade.doc.util.RequestResponseHolder;
import com.matchandtrade.doc.util.RequestResponseUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.ItemJson;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;


public class RestItemMaker extends OutputMaker {
	
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
		RequestResponseHolder tradeMembership = RestTradeMembershipMaker.buildPostJson("Board games in Montreal", null);
		TradeMembershipJson tradeMembershipJson = JsonUtil.fromHttpResponse(tradeMembership.getHttpResponse(), TradeMembershipJson.class);

		// ITEMS_POST_SNIPPET
		ItemJson postJson = new ItemJson();
		postJson.setName("Pandemic Legacy: Season 1");
		RequestResponseHolder post = RequestResponseUtil.buildPostRequestResponse(RestTradeMembershipMaker.BASE_URL + tradeMembershipJson.getTradeMembershipId() + BASE_URL, postJson);
		String postSnippet = TemplateUtil.buildSnippet(post.getHttpRequest(), post.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, ITEMS_POST_SNIPPET, postSnippet);
		postJson = JsonUtil.fromHttpResponse(post.getHttpResponse(), ItemJson.class);
		
		// ITEMS_PUT_SNIPPET
		Integer itemId = postJson.getItemId();
		postJson.setItemId(null); // Set as null because we do not want the id to be displayed on the request body to emphasize that the id must be sent on the URL
		postJson.setName(postJson.getName() + " After PUT");
		RequestResponseHolder put = RequestResponseUtil.buildPutRequestResponse(RestTradeMembershipMaker.BASE_URL + tradeMembershipJson.getTradeMembershipId() + BASE_URL + "/" + itemId , postJson);
		String putSnippet = TemplateUtil.buildSnippet(put.getHttpRequest(), put.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, ITEMS_PUT_SNIPPET, putSnippet);

		// ITEMS_GET_SNIPPET
		RequestResponseHolder get = RequestResponseUtil.buildGetRequestResponse(RestTradeMembershipMaker.BASE_URL + tradeMembershipJson.getTradeMembershipId() + BASE_URL + "/" + itemId);
		String getSnippet = TemplateUtil.buildSnippet(get.getHttpRequest(), get.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, ITEMS_GET_SNIPPET, getSnippet);

		// ITEMS_GET_ALL_SNIPPET
		RequestResponseHolder getAll = RequestResponseUtil.buildGetRequestResponse(RestTradeMembershipMaker.BASE_URL + tradeMembershipJson.getTradeMembershipId() + BASE_URL);
		String getAllSnippet = TemplateUtil.buildSnippet(getAll.getHttpRequest(), getAll.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, ITEMS_GET_ALL_SNIPPET, getAllSnippet);

		// ITEMS_SEARCH_SNIPPET
		RequestResponseHolder search = RequestResponseUtil.buildGetRequestResponse(RestTradeMembershipMaker.BASE_URL + tradeMembershipJson.getTradeMembershipId() + BASE_URL + "?name=Pandemic%20Legacy:%20Season%201%20After%20PUT");
		String searchSnippet = TemplateUtil.buildSnippet(search.getHttpRequest(), search.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, ITEMS_SEARCH_SNIPPET, searchSnippet);
		
		return template;
	}

	@Override
	public String getDocLocation() {
		return "items.html";
	}

}
