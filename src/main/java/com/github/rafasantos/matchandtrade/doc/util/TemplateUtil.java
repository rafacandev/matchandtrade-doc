package com.github.rafasantos.matchandtrade.doc.util;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.github.rafasantos.matchandtrade.exception.DocMakerException;

public class TemplateUtil {

	public static String replacePlaceholder(String template, String placeholder, String stamp) {
		return template.replace("${" + placeholder + "}", stamp);
	}

	public static String buildTemplate(String resourceLocation) {
		String result;
		try {
			String templatePath = TemplateUtil.class.getClassLoader().getResource(resourceLocation).getFile();
			File templateFile = new File(templatePath);
			result = FileUtils.readFileToString(templateFile, StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new DocMakerException("Not able to build template from resource: " + resourceLocation + ". Exception message: " + e.getMessage());
		}
		return result;
	}

}
