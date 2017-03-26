package com.github.rafasantos.matchandtrade.doc.maker;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.github.rafasantos.matchandtrade.doc.maker.OutputMaker;
import com.github.rafasantos.matchandtrade.exception.DocMakerException;

public class ReadmeMaker implements OutputMaker {

	private String filePath = this.getClass().getClassLoader().getResource("doc/README.md").getFile();
	
	public String buildDocOutputString() throws IOException {
		File file = new File(filePath);
		String fileAsString = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		return fileAsString;
	}
	
	@Override
	public String obtainDocContent() {
		String result = null;
		try {
			result = buildDocOutputString();
		} catch (Exception e) {
			throw new DocMakerException(this, e);
		}
		return result;
	}

	@Override
	public String obtainDocLocation() {
		return "README.md";
	}
}
