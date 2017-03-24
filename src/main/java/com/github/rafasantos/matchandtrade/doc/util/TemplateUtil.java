package com.github.rafasantos.matchandtrade.doc.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.github.rafasantos.matchandtrade.exception.DocMakerException;
import com.matchandtrade.config.AuthenticationProperties;

public class TemplateUtil {

	public static String replacePlaceholder(String template, String placeholder, String stamp) {
		return template.replace("${" + placeholder + "}", stamp);
	}

	public static String buildTemplate(String resourceLocation) {
		String result;
		try {
			String templatePath = TemplateUtil.class.getClassLoader().getResource(resourceLocation).getFile();
			File templateFile = new File(templatePath);
			result = FileUtils.readFileToString(templateFile, StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new DocMakerException("Not able to build template from resource: " + resourceLocation + ". Exception message: " + e.getMessage());
		}
		return result;
	}
	
	public static String buildRequestResponseOutput(HttpRequestBase httpRequest) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			CloseableHttpResponse httpResponse = httpClient.execute(httpRequest);
			
			
			StringBuilder headers = new StringBuilder();
			for (Header h : httpResponse.getAllHeaders()) {
				headers.append(h.getName() + ": ");
				headers.append(h.getValue());
			}
			StringBuilder result = new StringBuilder();;
			result.append(httpRequest.getMethod() + " " + httpRequest.getURI());
			
			
			result.append(httpResponse.getStatusLine().getProtocolVersion() + " ");
			result.append(httpResponse.getStatusLine().getStatusCode() + " ");
			result.append(Response.Status.fromStatusCode(httpResponse.getStatusLine().getStatusCode()).getReasonPhrase());
			Header[] authoHeaders = httpResponse.getHeaders(AuthenticationProperties.OAuth.AUTHORIZATION_HEADER.toString());
			result.append("\nHeaders: ");
			for (int i = 0; i < authoHeaders.length; i++) {
				result.append("\n\t" + authoHeaders[i].getName() + ": ");
				result.append(authoHeaders[i].getValue());
			}
			result.append("\n\n");
			
			
		} catch (Exception e) {
			throw new DocMakerException(e);
		}
		
		return null;
		
	}

}
