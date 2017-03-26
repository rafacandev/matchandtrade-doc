package com.github.rafasantos.matchandtrade.doc.maker.developmentguide;

import java.io.IOException;

import com.github.rafasantos.matchandtrade.doc.maker.OutputMaker;
import com.github.rafasantos.matchandtrade.doc.maker.rest.RestAuthenticateMaker;
import com.github.rafasantos.matchandtrade.doc.util.RequestResponseHolder;
import com.github.rafasantos.matchandtrade.doc.util.TemplateUtil;

public class DevelopmentGuide implements OutputMaker {
	

	private String buildDocContent() throws IOException {
		String template = TemplateUtil.buildTemplate("doc/development-guide/development-guide.md");
		RestAuthenticateMaker authenticate = new RestAuthenticateMaker();
		RequestResponseHolder rrHolder = authenticate.testPositive();
		String authenticateSnippet = authenticate.buildAuthenticatePositiveSnippet(rrHolder.getHttpRequest());
		template = TemplateUtil.replacePlaceholder(template, RestAuthenticateMaker.AUTHENTICATE_POSITIVE_SNIPPET, authenticateSnippet);
		return template;
	}

	@Override
	public String obtainDocContent() {
		String result = null;
		try {
			result = buildDocContent();
		} catch (IOException e) {
		}
		return result;
	}

	@Override
	public String obtainDocLocation() {
		return "development-guide/development-guide.md";
	}
}
