package com.github.rafasantos.matchandtrade.doc.util;

import java.io.File;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

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
	
	public static String buildRequestSnippet(HttpRequestBase httpRequest) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		StringBuilder result = new StringBuilder();;
		try {
			CloseableHttpResponse httpResponse = httpClient.execute(httpRequest);

			// Start snippet
			result.append("```\n");
			result.append("-----  Request  -----\n");
			// Request URL
			result.append(httpRequest.getMethod() + " " + httpRequest.getURI());
			result.append("\n");
			// Request headers
			StringBuilder headers = new StringBuilder();
			for (Header h : httpRequest.getAllHeaders()) {
				headers.append(h.getName() + ": ");
				headers.append(h.getValue());
			}
			if (headers.length() > 0) {
				result.append("\nHeaders: " + headers);
			}
			
			result.append("\n");
			result.append("-----  Response  -----\n");
			// Response details
			result.append(httpResponse.getStatusLine().getProtocolVersion() + " ");
			result.append(httpResponse.getStatusLine().getStatusCode() + " ");
			result.append(Response.Status.fromStatusCode(httpResponse.getStatusLine().getStatusCode()).getReasonPhrase());
			// Response headers
			Header[] authoHeaders = httpResponse.getHeaders(AuthenticationProperties.OAuth.AUTHORIZATION_HEADER.toString());
			result.append("\nHeaders: ");
			for (int i = 0; i < authoHeaders.length; i++) {
				result.append("\n\t" + authoHeaders[i].getName() + ": ");
				result.append(authoHeaders[i].getValue());
			}
			// Response body
			String responseBody;
			try {
				responseBody = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
			} catch (Exception e) {
				throw new DocMakerException(e);
			}
			result.append("\n\n");
			result.append(responseBody);
			result.append("\n");
			result.append("```");
		} catch (Exception e) {
			throw new DocMakerException(e);
		}
		return result.toString();
	}

}
