package com.matchandtrade.doc.maker;

import com.matchandtrade.doc.util.TemplateUtil;

public class CssMaker extends OutputMaker {

	public static final String CSS_LOCATION = "css/matchandtrade-doc-style.css";
	
	@Override
	public String buildDocContent() {
		return TemplateUtil.buildTemplate(getDocLocation());
	}

	@Override
	public String getDocLocation() {
		return CSS_LOCATION;
	}
	
	@Override
	public boolean requiresHeaderAndFooter() {
		return false;
	}
}
