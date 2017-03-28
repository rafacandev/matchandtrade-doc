package com.github.rafasantos.matchandtrade.doc.util;

import java.io.File;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

import com.github.rafasantos.matchandtrade.exception.DocMakerException;
import com.matchandtrade.config.AuthenticationProperties;

public class TemplateUtil {

	public static final String TEMPLATE_ROOT_LOCATION = "templates/";
	
	public static String replacePlaceholder(String template, String placeholder, String stamp) {
		return template.replace("${" + placeholder + "}", stamp);
	}

	public static String buildTemplate(String resourceLocation) {
		String result;
		try {
			String templatePath = TemplateUtil.class.getClassLoader().getResource(TEMPLATE_ROOT_LOCATION + resourceLocation).getFile();
			File templateFile = new File(templatePath);
			result = FileUtils.readFileToString(templateFile, StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new DocMakerException("Not able to build template from resource: " + resourceLocation + ". Exception message: " + e.getMessage());
		}
		return result;
	}
	
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
			if (httpRequest instanceof HttpPost) {
				result.append("\n");
				HttpPost httpPost = (HttpPost) httpRequest;
				String requestBody = IOUtils.toString(httpPost.getEntity().getContent(), StandardCharsets.UTF_8);
				requestBody = JsonUtil.prettyJson(requestBody);
				result.append(requestBody);
				result.append("\n");
			}
			result.append("\n");			
			result.append("-----  Response  -----\n");
			// Response details
			result.append("Status:   ");
			result.append(httpResponse.getStatusLine().getProtocolVersion() + " ");
			result.append(httpResponse.getStatusLine().getStatusCode() + " ");
			result.append(Response.Status.fromStatusCode(httpResponse.getStatusLine().getStatusCode()).getReasonPhrase());
			result.append("\n");
			// Response headers
			Header[] authoHeaders = httpResponse.getHeaders(AuthenticationProperties.OAuth.AUTHORIZATION_HEADER.toString());
			if (authoHeaders.length > 0) {
				result.append("Headers:  ");
				for (int i = 0; i < authoHeaders.length; i++) {
					result.append("{" + authoHeaders[i].getName() + ": ");
					result.append(authoHeaders[i].getValue() + "}");
				}
				result.append("\n");
			}
			result.append("\n");
			// Response body
			if (httpResponse.getEntity() != null) {
				String responseBody = "";
				try {
					responseBody = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
					responseBody = JsonUtil.prettyJson(responseBody);
				} catch (Exception e) {
					throw new DocMakerException(e);
				}
				result.append(responseBody);
				result.append("\n");
			}
			result.append("```");
		} catch (Exception e) {
			throw new DocMakerException(e);
		}
		return result.toString();
	}
}
