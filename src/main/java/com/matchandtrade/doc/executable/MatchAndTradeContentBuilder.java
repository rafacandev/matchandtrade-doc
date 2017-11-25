package com.matchandtrade.doc.executable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rafasantos.restdocmaker.RestDocMakerConfiguration;
import com.github.rafasantos.restdocmaker.RestDocMakerGenerator;
import com.matchandtrade.doc.maker.DevelopmentGuideRestDocMaker;
import com.matchandtrade.doc.maker.IndexRestDocMaker;
import com.matchandtrade.doc.maker.UseCaseRestDocMaker;
import com.matchandtrade.doc.maker.rest.AuthenticateRestDocMaker;
import com.matchandtrade.doc.maker.rest.AuthenticationRestDocMaker;
import com.matchandtrade.doc.maker.rest.ItemRestDocMaker;
import com.matchandtrade.doc.maker.rest.TradeRestDocMaker;
import com.matchandtrade.doc.maker.rest.TradeMembershipRestDocMaker;
import com.matchandtrade.doc.maker.rest.UserRestDocMaker;
import com.matchandtrade.doc.maker.rest.WantItemRestDocMaker;

public class MatchAndTradeContentBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(MatchAndTradeContentBuilder.class);
	private final String destinationFolder;
	
	public MatchAndTradeContentBuilder(String destinationFolder) {
		this.destinationFolder = destinationFolder;
	}

	public void buildContent() {
		try {
			RestDocMakerConfiguration config = new RestDocMakerConfiguration()
				.destinationFolderRootPath(destinationFolder)
				.makers(new DevelopmentGuideRestDocMaker(),
						new UseCaseRestDocMaker(),
						new AuthenticateRestDocMaker(),
						new AuthenticationRestDocMaker(),
						new TradeRestDocMaker(),
						new UserRestDocMaker(),
						new TradeMembershipRestDocMaker(),
						new ItemRestDocMaker(),
						new WantItemRestDocMaker(),
						new IndexRestDocMaker());
			RestDocMakerGenerator generator = new RestDocMakerGenerator();
			generator.generateDocumentation(config);
		} catch (Exception e) {
			LOGGER.error("\n\nFailed to generate documentation!\n\nException message {}\n", e.getMessage(), e);
			throw e;
		}
	}

}
