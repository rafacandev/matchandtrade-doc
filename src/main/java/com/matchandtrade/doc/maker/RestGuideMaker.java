package com.matchandtrade.doc.maker;

import com.matchandtrade.doc.util.TemplateUtil;

public class RestGuideMaker implements OutputMaker {
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		return template;
	}
	
	@Override
	public String getDocLocation() {
		return "doc/rest-guide.md";
	}
}
