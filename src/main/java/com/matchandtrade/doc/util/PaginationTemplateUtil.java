package com.matchandtrade.doc.util;

import com.github.rafasantos.restdocmaker.template.TemplateUtil;

public class PaginationTemplateUtil {

	private static final String PAGINATION_ROW_PLACEHOLDER = "" +
			"	<tr>\n" + 
			"		<td>_pageSize</td>\n" + 
			"		<td>See <a href='rest-guide.html'>pagination</a></td>\n" + 
			"	</tr>\n" + 
			"	<tr>\n" + 
			"		<td>_pageNumber</td>\n" + 
			"		<td>See <a href='rest-guide.html'>pagination</a></td>\n" + 
			"	</tr>\n" + 
			"";
	
	public static String replacePaginationRows(String template) {
		return TemplateUtil.replacePlaceholder(template, "PAGINATION_ROWS", PAGINATION_ROW_PLACEHOLDER);
	}
	
}