package com.matchandtrade.doc.maker;

import com.matchandtrade.doc.maker.OutputMaker;
import com.matchandtrade.doc.util.TemplateUtil;

public class DevelopmentGuide implements OutputMaker {

	@Override
	public String buildDocContent() {
		return TemplateUtil.buildTemplate(getDocLocation());
	}

	@Override
	public String getDocLocation() {
		return "doc/development-guide.md";
	}
}