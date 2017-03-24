package com.github.rafasantos.matchandtrade.doc.maker.developmentguide;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.github.rafasantos.matchandtrade.doc.maker.OutputMaker;
import com.github.rafasantos.matchandtrade.doc.maker.rest.RestAuthenticateMaker;

public class DevelopmentGuide implements OutputMaker {
	
	String filePath = this.getClass().getClassLoader().getResource("doc/development-guide/development-guide.md").getFile();
	
	private String buildDocOutputString() throws IOException {
		File file = new File(filePath);
		String fileAsString = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		return fileAsString;
	}

	@Override
	public void execute() {
		// Nothing to do execute for now
	}

	
	private String buildDocOutput() throws IOException {
		String fileString = buildDocOutputString();
		RestAuthenticateMaker authenticate = new RestAuthenticateMaker();
		String authenticateRequestPositive = authenticate.buildAuthenticateRequestOutputPositive();
		String authenticateResponsePositive = authenticate.buildAuthenticateResponseOutputPositive();
		fileString = fileString.replace("${" + RestAuthenticateMaker.AUTHENTICATE_POSITIVE_REQUEST_PLACEHOLDER + "}", authenticateRequestPositive);
		fileString = fileString.replace("${" + RestAuthenticateMaker.AUTHENTICATE_POSITIVE_RESPONSE_PLACEHOLDER + "}", authenticateResponsePositive);
		return fileString;
	}
	
	
	@Override
	public String getDocOutput() {
		String result = null;
		try {
			result = buildDocOutput();
		} catch (IOException e) {
		}
		return result;
	}

	@Override
	public String getDocOutputLocation() {
		return "development-guide/development-guide.md";
	}
}
