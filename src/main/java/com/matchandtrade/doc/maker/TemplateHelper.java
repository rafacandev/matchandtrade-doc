package com.matchandtrade.doc.maker;

import com.github.rafasantos.restdocmaker.exception.RestDocMakerException;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public class TemplateHelper {

    private static String rootDirectory = "templates/";

    /**
     * Builds a string from a file located at <pre>templateRelativePath</pre>.
     *
     * @param templateRelativePath
     */
    public static String buildTemplate(String templateRelativePath) {
        String result;
        String templatePath = TemplateUtil.class.getClassLoader().getResource(rootDirectory + templateRelativePath).getFile();
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
     * @param template
     * @return
     */
    public static String appendHeaderAndFooter(String template) {
        String header = "<html>"
            + "<head><title>REST Doc Maker</title></head>\n"
            + "<link rel='stylesheet' href='css/rest-doc-maker.css'>"
            + "<script src='js/rest-doc-maker.js'></script>"
            + "<body>\n";
        String footer = "</body>\n</html>\n";
        return header + template + footer;
    }

    /**
     * Replaces the <pre>placeholder</pre> by <pre>replacement</pre> in the given <pre>template</pre>.
     * @param template
     * @param placeholder
     * @param replacement
     */
    public static String replacePlaceholder(String template, String placeholder, String replacement) {
        if (!template.contains(placeholder)) {
            throw new RestDocMakerException("Not able to find any placeholder: ${" + placeholder +"}");
        }
        return template.replace("${" + placeholder + "}", replacement);
    }

}
