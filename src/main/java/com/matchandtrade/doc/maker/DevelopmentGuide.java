package com.matchandtrade.doc.maker;


import com.github.rafasantos.restdocmaker.template.TemplateUtil;

public class DevelopmentGuide extends OutputMaker {

	@Override
	public String buildDocContent() {
		return TemplateUtil.buildTemplate(getDocLocation());
	}

	@Override
	public String getDocLocation() {
		return "development-guide.html";
	}
}
