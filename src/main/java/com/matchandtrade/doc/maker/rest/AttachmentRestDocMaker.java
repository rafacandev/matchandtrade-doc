
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


public class AttachmentRestDocMaker implements RestDocMaker {
	
	private static final String POST_PLACEHOLDER = "POST_PLACEHOLDER";
	@Override
	public String contentFilePath() {
		return "attachments.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());
		String filePath = AttachmentRestDocMaker.class.getClassLoader().getResource("image-landscape.png").getFile();
		File file = new File(filePath);
		MultiPartSpecification fileSpec = new MultiPartSpecBuilder(file).mimeType("image/png").fileName("my-image.png").build();
		RequestSpecification attachmentRequest = new RequestSpecBuilder()
				.addHeaders(MatchAndTradeRestUtil.getLastAuthorizationHeaderAsMap())
				.addMultiPart(fileSpec)
				.build();
		Snippet attachmentSnippet = SnippetFactory.makeSnippet(Method.POST, attachmentRequest, MatchAndTradeRestUtil.filesUrl());
		attachmentSnippet.getResponse().then().statusCode(201);
		template = TemplateUtil.replacePlaceholder(template, POST_PLACEHOLDER, attachmentSnippet.asHtml());

		template = PaginationTemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

}
