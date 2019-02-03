package com.matchandtrade.doc.executable;

import com.matchandtrade.doc.document.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ContentBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContentBuilder.class);
	private final String destinationDirectory;
	private final String cssFilePath;
	private final String jsFilePath;

	public ContentBuilder(String destinationDirectory, String cssFilePath, String jsFilePath) {
		this.destinationDirectory = destinationDirectory;
		this.cssFilePath = cssFilePath;
		this.jsFilePath = jsFilePath;

	}

	public void buildContent() {
		try {
			ContentGenerator contentGenerator = new ContentGenerator(destinationDirectory, cssFilePath, jsFilePath);
			List<Document> contents = new ArrayList<>();
			contents.add(new ArticleDocument());
			contents.add(new AttachmentDocument());
			contents.add(new ArticleAttachmentDocument());
			contents.add(new AuthenticateDocument());
			contents.add(new AuthenticationDocument());
			contents.add(new IndexDocument());
			contents.add(new ListingDocument());
			contents.add(new MembershipDocument());
			contents.add(new OfferDocument());
			contents.add(new SearchDocument());
			contents.add(new TradeDocument());
			contents.add(new TradeResultDocument());
			contents.add(new TutorialDocument());
			contents.add(new UserDocument());

			contents.forEach(content -> {
				contentGenerator.generate(content);
			});

		} catch (Exception e) {
			LOGGER.error("\n\nFailed to generate documentation!\n\nException message {}\n", e.getMessage(), e);
			throw e;
		}
	}

}
