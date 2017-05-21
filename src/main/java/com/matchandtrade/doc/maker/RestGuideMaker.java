package com.matchandtrade.doc.maker;

import com.matchandtrade.doc.util.TemplateUtil;

public class RestGuideMaker implements OutputMaker {
	
	@Override
	public String buildDocContent() {
		return TemplateUtil.buildTemplate(getDocLocation());
	}
	
	@Override
	public String getDocLocation() {
		return "doc/rest-guide.md";
	}
}
