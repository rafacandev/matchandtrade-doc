
package com.matchandtrade.doc.document;

import com.matchandtrade.doc.util.TemplateUtil;


public class AttachmentDocument implements Document {
	
	private String template;

	public AttachmentDocument() {
		template = TemplateUtil.buildTemplate(contentFilePath());
	}

	@Override
	public String content() {
		template = TemplateUtil.replacePaginationTable(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

	@Override
	public String contentFilePath() {
		return "attachments.html";
	}

}
