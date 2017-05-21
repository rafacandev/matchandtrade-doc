package com.github.rafasantos.matchandtrade.doc.maker;

import java.io.IOException;

import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.exception.DocMakerException;

public class ReadmeMaker implements OutputMaker {

	public String buildDocOutputString() throws IOException {
		return TemplateUtil.buildTemplate(getDocLocation());
	}
	
	@Override
	public String buildDocContent() {
		String result = null;
		try {
			result = buildDocOutputString();
		} catch (Exception e) {
			throw new DocMakerException(this, e);
		}
		return result;
	}

	@Override
	public String getDocLocation() {
		return "README.md";
	}
}
