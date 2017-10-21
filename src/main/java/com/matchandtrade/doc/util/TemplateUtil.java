package com.matchandtrade.doc.util;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

import com.matchandtrade.exception.DocMakerException;

public class TemplateUtil {

	public static final String TEMPLATE_ROOT_LOCATION = "templates/";
	
	// Utility classes, which are a collection of static members, are not meant to be instantiated.
	private TemplateUtil() { }
	
	/**
	 * Build a snippet based on a HttpRequestBase and HttpResponse
	 * @param httpRequest
	 * @param httpResponse
	 * @return
	 */
	public static String buildSnippet(HttpRequestBase httpRequest, HttpResponse httpResponse) {
		StringBuilder snippet = new StringBuilder();;
		try {
			// Start snippet
			snippet.append("-----  Request  -----\n");
			// Request URL
			snippet.append(httpRequest.getMethod() + " " + httpRequest.getURI());
			snippet.append("\n");
			// Request headers
			snippet.append(buildHeadersString(httpRequest));
			// Request body
			if (httpRequest instanceof HttpPost || httpRequest instanceof HttpPut) {
				snippet.append("\n");
				HttpEntityEnclosingRequestBase requestBase = (HttpEntityEnclosingRequestBase) httpRequest;
				String requestBody = IOUtils.toString(requestBase.getEntity().getContent(), StandardCharsets.UTF_8);
				requestBody = JsonUtil.prettyJson(requestBody);
				snippet.append(requestBody);
				snippet.append("\n");
			}
			snippet.append("\n");			
			snippet.append("-----  Response  -----\n");
			// Response details
			snippet.append("Status:   ");
			snippet.append(httpResponse.getStatusLine());
			snippet.append("\n");
			// Response headers
			snippet.append(buildHeadersString(httpResponse));
			// Response body
			if (httpResponse.getEntity() != null) {
				String responseBody = "";
				try {
					responseBody = RestUtil.buildResponseBodyString(httpResponse);
					responseBody = JsonUtil.prettyJson(responseBody);
				} catch (Exception e) {
					throw new DocMakerException(e);
				}
				if (responseBody != null && responseBody.length() > 0) {
					snippet.append("\n");
					snippet.append(responseBody);
				}
			}
		} catch (Exception e) {
			throw new DocMakerException(e);
		}

		return "<div class='code'>" + StringEscapeUtils.escapeHtml(snippet.toString()) + "\n</div>";
	}

	private static String buildHeadersString(HttpMessage httpRequest) {
		StringBuilder result = new StringBuilder();
		Header[] headers = httpRequest.getAllHeaders();
		if (headers.length > 0) {
			result.append("Headers:  ");
			for (int i = 0; i < headers.length; i++) {
				if (i > 0) {
					result.append("\n          ");
				}
				result.append(headers[i].getName() + ": ");
				result.append(headers[i].getValue());
			}
			result.append("\n");
		}
		return result.toString();
	}

	/**
	 * Build a string representation for a given template
	 * 
	 * @param templateRelativePath
	 * @return
	 */
	public static String buildTemplate(String templateRelativePath) {
		String result;
		try {
			String templatePath = TemplateUtil.class.getClassLoader().getResource(TEMPLATE_ROOT_LOCATION + templateRelativePath).getFile();
			File templateFile = new File(templatePath);
			result = FileUtils.readFileToString(templateFile, StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new DocMakerException("Not able to build template from resource: " + templateRelativePath + ". Exception message: " + e.getMessage());
		}
		return result;
	}

	public static String replacePlaceholder(String template, String placeholder, String replacement) {
		if (!template.contains(placeholder)) {
			throw new DocMakerException("Not able to find any placeholder: ${" + placeholder +"}");
		}
		return template.replace("${" + placeholder + "}", replacement);
	}

}
