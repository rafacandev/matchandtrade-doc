package com.matchandtrade.doc.executable;

import com.matchandtrade.doc.document.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ContentBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContentBuilder.class);
	private final String destinationFolder;

	public ContentBuilder(String destinationFolder) {
		this.destinationFolder = destinationFolder;
	}

	public void buildContent() {
		try {
			// TODO Remove hardcoded css
			ContentGenerator contentGenerator = new ContentGenerator(destinationFolder, "/templates/css/rest-api-doc.css");
			List<Document> contents = new ArrayList<>();
			contents.add(new IndexDocument());
			contents.add(new AuthenticateDocument());
			contents.add(new AuthenticationDocument());
			contents.add(new UserDocument());
			contents.add(new TradeDocument());
			contents.add(new MembershipDocument());
			contents.add(new ArticleDocument());
			contents.add(new OfferDocument());
			contents.add(new TradeResultDocument());
			contents.add(new SearchDocument());
			contents.add(new ListingDocument());
			contents.add(new TutorialDocument());
			contents.add(new AttachmentDocument());
			contents.add(new ArticleAttachmentDocument());

			contents.forEach(content -> {
				contentGenerator.generate(content);
			});

		} catch (Exception e) {
			LOGGER.error("\n\nFailed to generate documentation!\n\nException message {}\n", e.getMessage(), e);
			throw e;
		}
	}

}
