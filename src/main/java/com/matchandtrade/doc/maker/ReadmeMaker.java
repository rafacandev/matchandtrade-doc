package com.matchandtrade.doc.maker;

import com.matchandtrade.doc.util.TemplateUtil;

public class ReadmeMaker implements OutputMaker {

	@Override
	public String buildDocContent() {
		return TemplateUtil.buildTemplate(getDocLocation());
	}

	@Override
	public String getDocLocation() {
		return "README.md";
	}
}
