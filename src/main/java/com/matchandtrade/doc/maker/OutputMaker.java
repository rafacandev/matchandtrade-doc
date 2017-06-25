package com.matchandtrade.doc.maker;

public abstract class OutputMaker {

	public abstract String buildDocContent();

	public abstract String getDocLocation();

	public boolean requiresHeaderAndFooter() {
		return true;
	}

}
