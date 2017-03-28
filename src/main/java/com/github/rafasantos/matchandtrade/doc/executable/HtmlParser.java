package com.github.rafasantos.matchandtrade.doc.executable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ext.abbreviation.AbbreviationExtension;
import com.vladsch.flexmark.ext.definition.DefinitionExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.typographic.TypographicExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.options.MutableDataSet;

/**
 * Utility class to parse from MarkDown to HTML.
 * There is a known problem related to links where links to other MarkDown files are not converted to HTML.
 * To circumvent this limitation the {@code partyMarkDownToHtml()} will parse "numeric reference-style" links from ".md" to ".md.html".
 * E.g.: {@code [1]: doc/ref.md} will be parsed as {@code [1]: doc/ref.md.html}
 * 
 * @author rafael.santos.bra@gmail.com
 *
 */
public class HtmlParser {
	
	private MutableDataSet mdOptions;
	private Parser mdParser;
	private HtmlRenderer htmlRenderer;
	private static String styleHead = null;
	private static String styleTail = null;
	
	public HtmlParser() {
		mdOptions  = new MutableDataSet();
        mdOptions.setFrom(ParserEmulationProfile.GITHUB_DOC);
        mdOptions.set(Parser.EXTENSIONS, Arrays.asList(
                AbbreviationExtension.create(),
                DefinitionExtension.create(),
                FootnoteExtension.create(),
                TypographicExtension.create(),
                TablesExtension.create()
        ));
		mdParser = Parser.builder(mdOptions).build();
		htmlRenderer = HtmlRenderer.builder(mdOptions).build();
	}
	
	public String parseMarkDownToHTML(String md) {
		String mdWithCorrectedLinks = correctMardDownLinks(md);
		Node htmlNode = mdParser.parse(mdWithCorrectedLinks);
		String result = htmlRenderer.render(htmlNode);
		return result;
	}

	private String correctMardDownLinks(String md) {
		StringBuilder mdWithParsedLinks = new StringBuilder();
		String[] lines = md.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			// TODO make this better so it detects: If starts with [ followed by digits followed by ]:
			if (line.startsWith("[") && Character.isDigit(line.charAt(1))) {
				line = line.replace(".md", ".md.html");
			}
			mdWithParsedLinks.append(line + "\n");
		}
		String mdWithCorrectedLinks = mdWithParsedLinks.toString();
		return mdWithCorrectedLinks;
	}

	public static File optainHtmlFile(File mdFile) {
		return new File(mdFile.getAbsolutePath() + ".html");
	}
	
	/**
	 * Returns a GitHub like CSS style to be included at the beginning of a HTML file. So, you mimics GitHub MarkDown appearance.
	 * @return GitHub CSS style head 
	 * @throws IOException
	 */
	public static String getGitHubStyleHead() throws IOException {
		if (styleHead == null) {
			String includeHeaderFile = HtmlParser.class.getClassLoader().getResource("markdown-to-html-github-style-include-head.txt").getFile();
			styleHead = FileUtils.readFileToString(new File(includeHeaderFile), StandardCharsets.UTF_8);
		}
		return styleHead;
	}

	/**
	 * Returns a GitHub like CSS style to be included at the end of a HTML file. So, you mimics GitHub MarkDown appearance.
	 * @return GitHub CSS style tail
	 * @throws IOException
	 */
	public static String getGitHubStyleTail() throws IOException {
		if (styleTail == null) {
			String footerHeaderFile = HtmlParser.class.getClassLoader().getResource("markdown-to-html-github-style-include-head.txt").getFile();
			styleTail = FileUtils.readFileToString(new File(footerHeaderFile), StandardCharsets.UTF_8);
		}
		return styleTail;
	}

}