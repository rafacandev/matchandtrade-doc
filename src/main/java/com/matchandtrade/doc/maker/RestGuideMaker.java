package com.matchandtrade.doc.maker;

import com.matchandtrade.doc.maker.rest.RestTradeMaker;
import com.matchandtrade.doc.util.AssertUtil;
import com.matchandtrade.doc.util.RequestResponseHolder;
import com.matchandtrade.doc.util.RequestResponseUtil;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.TradeJson;

public class RestGuideMaker implements OutputMaker {
	
	public static final String REST_GUIDE_PAGINATION = "REST_GUIDE_PAGINATION";
	
	@Override
	public String buildDocContent() {

		// Create some trades for the pagination
		TradeJson tradeJson = new TradeJson();
		tradeJson.setName("Board games in Quebec");
		RequestResponseUtil.buildPostRequestResponse(RestTradeMaker.BASE_URL + "/", tradeJson);
		// Pagination Snippet
		String template = TemplateUtil.buildTemplate(getDocLocation());
		RequestResponseHolder paginationRRH = RequestResponseUtil.buildGetRequestResponse(RestTradeMaker.BASE_URL + "?_pageNumber=2&_pageSize=2");
		String paginationSnippet = TemplateUtil.buildSnippet(paginationRRH.getHttpRequest(), paginationRRH.getHttpResponse());
		AssertUtil.isTrue(paginationSnippet.contains("nextPage"));
		AssertUtil.isTrue(paginationSnippet.contains("previousPage"));
		return TemplateUtil.replacePlaceholder(template, REST_GUIDE_PAGINATION, paginationSnippet);
	}
	
	@Override
	public String getDocLocation() {
		return "doc/rest-guide.md";
	}
}
