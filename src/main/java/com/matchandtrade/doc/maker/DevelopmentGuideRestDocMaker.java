package com.matchandtrade.doc.maker;


import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;

public class DevelopmentGuideRestDocMaker implements RestDocMaker {

	@Override
	public String contentFilePath() {
		return "development-guide.html";
	}

	@Override
	public String content() {
		return TemplateUtil.buildTemplate(contentFilePath());
	}

}
