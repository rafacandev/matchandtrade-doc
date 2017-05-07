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
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		
		// Create a trade membership
		RequestResponseHolder tradeMembershipRRH = RestTradeMembershipsMaker.buildPostJson("Board games in Montreal");
		TradeMembershipJson tradeMembership = JsonUtil.fromHttpResponse(tradeMembershipRRH.getHttpResponse(), TradeMembershipJson.class);

		// POST
		ItemJson postJson = new ItemJson();
		postJson.setName("Pandemic Legacy: Season 1");
		RequestResponseHolder post = SnippetUtil.buildPostRequestResponse(RestTradeMembershipsMaker.BASE_URL + tradeMembership.getTradeMembershipId() + BASE_URL, postJson);
		String postSnippet = TemplateUtil.buildSnippet(post.getHttpRequest(), post.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, ITEMS_POST_SNIPPET, postSnippet);
		
		return template;
	}

	@Override
	public String getDocLocation() {
		return "doc/rest/items.md";
	}

}
