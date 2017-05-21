package com.matchandtrade.doc.util;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

import com.matchandtrade.config.AuthenticationProperties;
import com.matchandtrade.exception.DocMakerException;

public class TemplateUtil {

	public static final String TEMPLATE_ROOT_LOCATION = "templates/";
	
	// Utility classes, which are a collection of static members, are not meant to be instantiated.
	private TemplateUtil() { }
	
	private static Header[] buildResponseHeaders(HttpResponse httpResponse) {
		Header[] authorizationHeaders = httpResponse.getHeaders(AuthenticationProperties.OAuth.AUTHORIZATION_HEADER.toString());
		Header[] paginationTotalCount = httpResponse.getHeaders("X-Pagination-Total-Count");
		Header[] responseHeaders = (Header[]) ArrayUtils.addAll(authorizationHeaders, paginationTotalCount);
		Header[] linkHeaders = httpResponse.getHeaders("Link");
		responseHeaders = (Header[]) ArrayUtils.addAll(responseHeaders, linkHeaders);
		return responseHeaders;
	}

	/**
	 * Build a snippet based on a HttpRequestBase and HttpResponse
	 * @param httpRequest
	 * @param httpResponse
	 * @return
	 */
	public static String buildSnippet(HttpRequestBase httpRequest, HttpResponse httpResponse) {
		StringBuilder result = new StringBuilder();;
		try {
			// Start snippet
			result.append("```\n");
			result.append("-----  Request  -----\n");
			// Request URL
			result.append(httpRequest.getMethod() + " " + httpRequest.getURI());
			result.append("\n");
			// Request headers
			Header[] headers = httpRequest.getAllHeaders();
			if (headers.length > 0) {
				result.append("Headers:  ");
				for (int i = 0; i < headers.length; i++) {
					result.append("{" + headers[i].getName() + ": ");
					result.append(headers[i].getValue() + "}");
				}
				result.append("\n");
			}
			// Request body
			if (httpRequest instanceof HttpPost || httpRequest instanceof HttpPut) {
				result.append("\n");
				HttpEntityEnclosingRequestBase requestBase = (HttpEntityEnclosingRequestBase) httpRequest;
				String requestBody = IOUtils.toString(requestBase.getEntity().getContent(), StandardCharsets.UTF_8);
				requestBody = JsonUtil.prettyJson(requestBody);
				result.append(requestBody);
				result.append("\n");
			}
			result.append("\n");			
			result.append("-----  Response  -----\n");
			// Response details
			result.append("Status:   ");
			result.append(httpResponse.getStatusLine());
			result.append("\n");
			// Response headers
			Header[] responseHeaders = buildResponseHeaders(httpResponse);
			if (responseHeaders.length > 0) {
				result.append("Headers:  ");
				for (int i = 0; i < responseHeaders.length; i++) {
					result.append("{" + responseHeaders[i].getName() + ": ");
					result.append(responseHeaders[i].getValue() + "}");
				}
				result.append("\n");
			}
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
					result.append("\n");
					result.append(responseBody);
				}
			}
			result.append("\n```");
		} catch (Exception e) {
			throw new DocMakerException(e);
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
		return template.replace("${" + placeholder + "}", replacement);
	}

}
