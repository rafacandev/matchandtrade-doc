package com.matchandtrade.doc.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public class TemplateUtil {

    private static final String TEMPLATE_DIRECTORY = "templates/";
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

    /**
     * Builds a string from a file located at <pre>templateRelativePath</pre>.
     *
     * @param templateRelativePath
     */
    public static String buildTemplate(String templateRelativePath) {
        String result;
        String templatePath = TemplateUtil.class.getClassLoader().getResource(TEMPLATE_DIRECTORY + templateRelativePath).getFile();
        File templateFile = new File(templatePath);
        try {
            result = FileUtils.readFileToString(templateFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return result;
    }

    /**
     * Append the <pre>header</pre> and <pre>footer</pre> to the template.
     *
     * @param template
     * @return
     */
    public static String appendHeaderAndFooter(String template) {
        String header = "<html>"
            + "<head><title>REST Doc Maker</title></head>\n"
            + "<link rel='stylesheet' href='css/rest-api-doc.css'>"
            + "<script src='js/rest-api-doc.js'></script>"
            + "<body>\n";
        String footer = "</body>\n</html>\n";
        return header + template + footer;
    }

    /**
     * Replaces the <pre>placeholder</pre> by <pre>replacement</pre> in the given <pre>template</pre>.
     *
     * @param template
     * @param placeholder
     * @param replacement
     */
    public static String replacePlaceholder(String template, String placeholder, String replacement) {
        if (!template.contains(placeholder)) {
            IOException exception = new IOException("Not able to find placeholder: ${" + placeholder +"}");
            throw new UncheckedIOException(exception);
        }
        return template.replace("${" + placeholder + "}", replacement);
    }

    public static String replacePaginationRows(String template) {
        return replacePlaceholder(template, "PAGINATION_ROWS", PAGINATION_ROW_PLACEHOLDER);
    }

    public static String replacePaginationTable(String template) {
        return replacePlaceholder(template, "PAGINATION_TABLE", PAGINATION_TABLE_PLACEHOLDER);
    }

}
