package com.matchandtrade.doc.util;

// TODO insert in TemplateUtil
public class PaginationTemplateUtil {

	private static final String PAGINATION_ROW_PLACEHOLDER = "" +
			"	<tr>\n" + 
			"		<td>_pageNumber</td>\n" + 
			"		<td>See <a href='index.html#pagination'>pagination</a></td>\n" + 
			"	</tr>\n" + 
			"	<tr>\n" + 
			"		<td>_pageSize</td>\n" + 
			"		<td>See <a href='index.html#pagination'>pagination</a></td>\n" + 
			"	</tr>\n" + 
			"";
	private static final String PAGINATION_TABLE_PLACEHOLDER = "" +
			"<table>\n" + 
			"	<tr>\n" + 
			"		<th>Property Name</th>\n" + 
			"		<th>Description</th>\n" + 
			"	</tr>\n" + 
			PAGINATION_ROW_PLACEHOLDER + 
			"</table>\n" + 
			""+
			"";
	
	public static String replacePaginationRows(String template) {
		return TemplateUtil.replacePlaceholder(template, "PAGINATION_ROWS", PAGINATION_ROW_PLACEHOLDER);
	}

	public static String replacePaginationTable(String template) {
		return TemplateUtil.replacePlaceholder(template, "PAGINATION_TABLE", PAGINATION_TABLE_PLACEHOLDER);
	}
	
}