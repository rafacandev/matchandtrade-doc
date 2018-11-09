package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.clientapi.MatchAndTradeClient;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.MembershipJson;
import com.matchandtrade.rest.v1.json.TradeJson;


public class MembershipDocument implements Document {
	
	private static final String MEMBERSHIPS_POST_PLACEHOLDER = "MEMBERSHIPS_POST_PLACEHOLDER";
	private static final String MEMBERSHIPS_GET_PLACEHOLDER = "MEMBERSHIPS_GET_PLACEHOLDER";
	private static final String MEMBERSHIPS_GET_ALL_PLACEHOLDER = "MEMBERSHIPS_GET_ALL_PLACEHOLDER";
	private static final String MEMBERSHIPS_SEARCH_PLACEHOLDER = "MEMBERSHIPS_SEARCH_PLACEHOLDER";
	private static final String MEMBERSHIPS_DELETE_PLACEHOLDER = "MEMBERSHIPS_DELETE_PLACEHOLDER";

	private final MatchAndTradeClient clientApi;
	private String template;

	public MembershipDocument() {
		clientApi = new MatchAndTradeClient();
		template = TemplateUtil.buildTemplate(contentFilePath());
	}

	@Override
	public String contentFilePath() {
		return "memberships.html";
	}

	@Override
	public String content() {
		// MEMBERSHIPS_POST_PLACEHOLDER
		MembershipJson membership = buildMembership();
		SpecificationParser postMembershipParser = clientApi.create(membership);
		template = TemplateUtil.replacePlaceholder(template, MEMBERSHIPS_POST_PLACEHOLDER, postMembershipParser.asHtmlSnippet());
		membership = postMembershipParser.getResponse().body().as(MembershipJson.class);

		// MEMBERSHIPS_GET_PLACEHOLDER
		SpecificationParser getMembershipParser = clientApi.findMembership(membership.getMembershipId());
		template = TemplateUtil.replacePlaceholder(template, MEMBERSHIPS_GET_PLACEHOLDER, getMembershipParser.asHtmlSnippet());

		// MEMBERSHIPS_GET_ALL_PLACEHOLDER
		SpecificationParser getAllMembershipsParser = clientApi.findMemberships(1, 3);
		template = TemplateUtil.replacePlaceholder(template, MEMBERSHIPS_GET_ALL_PLACEHOLDER, getAllMembershipsParser.asHtmlSnippet());

		// MEMBERSHIPS_SEARCH_PLACEHOLDER
		Integer userId = clientApi.getUserId();
		SpecificationParser searchMembershipParser = clientApi.findMembershipByUserIdOrTradeId(userId, null);
		template = TemplateUtil.replacePlaceholder(template, MEMBERSHIPS_SEARCH_PLACEHOLDER, searchMembershipParser.asHtmlSnippet());

		// MEMBERSHIPS_DELETE_PLACEHOLDER
		SpecificationParser deleteMembershipParser = clientApi.deleteMembership(membership.getMembershipId());
		template = TemplateUtil.replacePlaceholder(template, MEMBERSHIPS_DELETE_PLACEHOLDER, deleteMembershipParser.asHtmlSnippet());

		template = TemplateUtil.replacePaginationRows(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

	private MembershipJson buildMembership() {
		TradeJson trade = buildTrade();
		MembershipJson membership = new MembershipJson();
		membership.setTradeId(trade.getTradeId());
		membership.setUserId(clientApi.getUserId());
		return membership;
	}

	private TradeJson buildTrade() {
		MatchAndTradeClient ownerClientApi = new MatchAndTradeClient();
		TradeJson trade = new TradeJson();
		trade.setName("Board games in Vancouver - " + System.currentTimeMillis());
		return ownerClientApi.create(trade).getResponse().as(TradeJson.class);
	}

}
