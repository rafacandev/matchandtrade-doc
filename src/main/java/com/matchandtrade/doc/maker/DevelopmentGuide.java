package com.matchandtrade.doc.maker;

import com.matchandtrade.doc.maker.OutputMaker;
import com.matchandtrade.doc.util.TemplateUtil;

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
