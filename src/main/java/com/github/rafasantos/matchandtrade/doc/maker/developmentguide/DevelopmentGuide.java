package com.github.rafasantos.matchandtrade.doc.maker.developmentguide;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.github.rafasantos.matchandtrade.doc.maker.OutputMaker;
import com.github.rafasantos.matchandtrade.doc.maker.rest.RestAuthenticateMaker;
import com.github.rafasantos.matchandtrade.doc.util.RequestResponseHolder;
import com.github.rafasantos.matchandtrade.doc.util.TemplateUtil;

public class DevelopmentGuide implements OutputMaker {
	
	String filePath = this.getClass().getClassLoader().getResource("doc/development-guide/development-guide.md").getFile();
	
	private String buildDocOutputString() throws IOException {
		File file = new File(filePath);
		String fileAsString = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		return fileAsString;
	}

	private String buildDocOutput() throws IOException {
		String template = buildDocOutputString();
		RestAuthenticateMaker authenticate = new RestAuthenticateMaker();
		RequestResponseHolder rrHolder = authenticate.testPositive();
		String authenticateSnippet = authenticate.buildAuthenticatePositiveSnippet(rrHolder.getHttpRequest(), rrHolder.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, RestAuthenticateMaker.AUTHENTICATE_POSITIVE_PLACEHOLDER, authenticateSnippet);
		return template;
	}
	
	@Override
	public String obtainDocContent() {
		String result = null;
		try {
			result = buildDocOutput();
		} catch (IOException e) {
		}
		return result;
	}

	@Override
	public String obtainDocLocation() {
		return "development-guide/development-guide.md";
	}
}
