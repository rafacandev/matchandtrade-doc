
package com.matchandtrade.doc.maker.rest;

import java.io.File;

import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.PaginationTemplateUtil;

import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.Method;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;


public class FileRestDocMaker implements RestDocMaker {
	
	private static final String POST_PLACEHOLDER = "POST_PLACEHOLDER";
	@Override
	public String contentFilePath() {
		return "files.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());
		String filePath = FileRestDocMaker.class.getClassLoader().getResource("image-landscape.png").getFile();
		File file = new File(filePath);
		MultiPartSpecification fileSpec = new MultiPartSpecBuilder(file).mimeType("image/png").fileName("my-image.png").build();
		RequestSpecification fileRequest = new RequestSpecBuilder()
				.addHeaders(MatchAndTradeRestUtil.getLastAuthorizationHeaderAsMap())
				.addMultiPart(fileSpec)
				.build();
		Snippet fileSnippet = SnippetFactory.makeSnippet(Method.POST, fileRequest, MatchAndTradeRestUtil.filesUrl());
		fileSnippet.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, POST_PLACEHOLDER, fileSnippet.asHtml());

		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

}
