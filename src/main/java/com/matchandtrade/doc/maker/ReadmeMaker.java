package com.matchandtrade.doc.maker;

import com.matchandtrade.doc.util.TemplateUtil;

public class ReadmeMaker extends OutputMaker {

	@Override
	public String buildDocContent() {
		return TemplateUtil.buildTemplate(getDocLocation());
	}

	@Override
	public String getDocLocation() {
		return "index.html";
	}
}
