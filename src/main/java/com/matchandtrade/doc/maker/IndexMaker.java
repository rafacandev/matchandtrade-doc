package com.matchandtrade.doc.maker;

import com.matchandtrade.doc.util.TemplateUtil;

public class IndexMaker extends OutputMaker {

	public static final String REST_GUIDE_PAGINATION = "REST_GUIDE_PAGINATION";
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());
//		// REST_GUIDE_PAGINATION
//		// Assuming that there are already other trades created by the other makers
//		TradeJson tradeJson = new TradeJson();
//		tradeJson.setName("Board games in Quebec");
//		RequestResponseUtil.buildPostRequestResponse(RestTradeMaker.BASE_URL + "/", tradeJson);
//		
//		RequestResponseHolder paginationRRH = RequestResponseUtil.buildGetRequestResponse(RestTradeMaker.BASE_URL + "?_pageNumber=2&_pageSize=2");
//		String paginationSnippet = TemplateUtil.buildSnippet(paginationRRH.getHttpRequest(), paginationRRH.getHttpResponse());
//		// We want to make sure the pagination contains nextPage & previousPage
//		AssertUtil.isTrue(paginationSnippet.contains("nextPage"));
//		AssertUtil.isTrue(paginationSnippet.contains("previousPage"));
//		
//		return TemplateUtil.replacePlaceholder(template, REST_GUIDE_PAGINATION, paginationSnippet);
		return template;
	}

	@Override
	public String getDocLocation() {
		return "index.html";
	}
}
