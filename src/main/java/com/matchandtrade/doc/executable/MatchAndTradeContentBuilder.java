package com.matchandtrade.doc.executable;

import com.matchandtrade.doc.maker.DocumentContent;
import com.matchandtrade.doc.maker.IndexRestDocMaker;
import com.matchandtrade.doc.maker.UseCaseRestDocMaker;
import com.matchandtrade.doc.maker.rest.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MatchAndTradeContentBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(MatchAndTradeContentBuilder.class);
	private final String destinationFolder;

	public MatchAndTradeContentBuilder(String destinationFolder) {
		this.destinationFolder = destinationFolder;
	}

	public void buildContent() {
		try {
			// TODO Remove hardcoded css
			ContentGenerator contentGenerator = new ContentGenerator(destinationFolder, "/templates/css/rest-api-doc.css");
			List<DocumentContent> contents = new ArrayList<>();
			contents.add(new IndexRestDocMaker());
			contents.add(new AuthenticateRestDocMaker());
			contents.add(new AuthenticationRestDocMaker());
			contents.add(new UserRestDocMaker());
			contents.add(new TradeRestDocMaker());
			contents.add(new MembershipRestDocMaker());
			contents.add(new ArticleRestDocMaker());
			contents.add(new OfferRestDocMaker());
			contents.add(new TradeResultRestDocMaker());
			contents.add(new SearchRestDocMaker());
			contents.add(new UseCaseRestDocMaker());
			contents.add(new AttachmentRestDocMaker());
			contents.add(new ArticleAttachmentRestDocMaker());

			contents.forEach(content -> {
				contentGenerator.generate(content);
			});

		} catch (Exception e) {
			LOGGER.error("\n\nFailed to generate documentation!\n\nException message {}\n", e.getMessage(), e);
			throw e;
		}
	}

}
