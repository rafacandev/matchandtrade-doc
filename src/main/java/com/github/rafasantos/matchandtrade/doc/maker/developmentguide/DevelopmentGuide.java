package com.github.rafasantos.matchandtrade.doc.maker.developmentguide;

import com.github.rafasantos.matchandtrade.doc.maker.OutputMaker;
import com.github.rafasantos.matchandtrade.doc.util.TemplateUtil;

public class DevelopmentGuide implements OutputMaker {

	@Override
	public String obtainDocContent() {
		return TemplateUtil.buildTemplate("doc/development-guide/development-guide.md");
	}

	@Override
	public String obtainDocLocation() {
		return "development-guide/development-guide.md";
	}
}
