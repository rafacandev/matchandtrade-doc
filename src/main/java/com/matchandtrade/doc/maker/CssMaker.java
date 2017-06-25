package com.matchandtrade.doc.maker;

import com.matchandtrade.doc.util.TemplateUtil;

public class CssMaker extends OutputMaker {

	@Override
	public String buildDocContent() {
		String semantic = TemplateUtil.buildTemplate("css/semantic.min.css");
		String customCss = "\n\n"
				+ "body {margin-left:10px; margin-right:10px; line-height: 1.5;}\n"
				+ "table, td, th {border: 1px solid black;padding:5px;}\n"
				+ "th {background-color: #CFFED7;}\n"
				+ ".code {background-color: #EAF6EC; font-family: monospace; white-space: pre; font-size:small; padding: 5px; margin: 10px;}\n"
				+ ".incode {background-color: #EAF6EC; font-family: monospace; white-space: pre; font-size:small; padding: 3px;}\n";
		return semantic + customCss;
	}

	@Override
	public String getDocLocation() {
		return "css/combined-style.css";
	}
	
	@Override
	public boolean requiresHeaderAndFooter() {
		return false;
	}
}
