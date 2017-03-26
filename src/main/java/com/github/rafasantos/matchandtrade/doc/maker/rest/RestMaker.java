package com.github.rafasantos.matchandtrade.doc.maker.rest;

import com.github.rafasantos.matchandtrade.doc.maker.OutputMaker;
import com.github.rafasantos.matchandtrade.doc.util.TemplateUtil;

public class RestMaker implements OutputMaker {
	
	@Override
	public String obtainDocContent() {
		return TemplateUtil.buildTemplate("doc/rest/rest.md");
	}

	@Override
	public String obtainDocLocation() {
		return "rest/rest.md";
	}
}
