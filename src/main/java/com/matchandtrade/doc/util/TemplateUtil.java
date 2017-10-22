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
	
	private static int tabIdSequence = 0;
	
	// Utility classes, which are a collection of static members, are not meant to be instantiated.
	private TemplateUtil() { }
	
	/**
	 * Build a snippet based on a HttpRequestBase and HttpResponse
	 * @param httpRequest
	 * @param httpResponse
	 * @return
	 */
	public static String buildSnippet(HttpRequestBase httpRequest, HttpResponse httpResponse) {
		String summarySnippet = buildSummarySnippet(httpRequest, httpResponse);
		summarySnippet = StringEscapeUtils.escapeHtml(summarySnippet);

		String detailedSnippet = buildDetailedSnippet(httpRequest, httpResponse);
		detailedSnippet = StringEscapeUtils.escapeHtml(detailedSnippet.toString());
		
		int tabId = nextTabId();
		
		String summaryTabId = "summaryTab_" + tabId;
		String httpTabId = "httpTab_" + tabId;
		String curlTabId = "curlTab_" + tabId;
		
		return    "<div class='tab'>"
				+   "<button id='summaryTabLink_"+tabId+"' class='tablinks' onclick='openTab(event, \""+summaryTabId+"\")'>Summary</button>"
				+   "<button id='httpTabLink_"+tabId+"' class='tablinks' onclick='openTab(event, \""+httpTabId+"\")'>HTTP</button>"
				+   "<button id='curlTabLink_"+tabId+"' class='tablinks' onclick='openTab(event, \""+curlTabId+"\")'>curl</button>"
				+ "</div>"
				+ "<div id='"+summaryTabId+"' class='tabcontent code'>" + summarySnippet + "</div>"
				+ "<div id='"+httpTabId+"' class='tabcontent code'>" + detailedSnippet + "</div>"
				+ "<div id='"+curlTabId+"' class='tabcontent code'>" + "......" + "\n</div>"
				+ "<script>openTabById('summaryTabLink_"+tabId+"', \""+summaryTabId+"\");</script>";
	}

	private static String buildDetailedSnippet(HttpRequestBase httpRequest, HttpResponse httpResponse) {
		StringBuilder result = new StringBuilder();
		try {
			// Start snippet
			result.append("-----  Request  -----\n");
			// Request URL
			result.append(httpRequest.getMethod() + " " + httpRequest.getURI());
			result.append("\n");
			// Request headers
			result.append(buildHeadersString(httpRequest));
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
			result.append(buildHeadersString(httpResponse));
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
		} catch (Exception e) {
			throw new DocMakerException(e);
		}
		return result.toString();
	}

	private static String buildSummarySnippet(HttpRequestBase httpRequest, HttpResponse httpResponse) {
		StringBuilder result = new StringBuilder();
		try {
			// Request URL
			result.append(httpRequest.getMethod() + " " + httpRequest.getURI() + "\n");
			// Request body
			if (httpRequest instanceof HttpPost || httpRequest instanceof HttpPut) {
				HttpEntityEnclosingRequestBase requestBase = (HttpEntityEnclosingRequestBase) httpRequest;
				String requestBody = "Request:  " + IOUtils.toString(requestBase.getEntity().getContent(), StandardCharsets.UTF_8);
				requestBody = requestBody.replace("\n", "");
				if (requestBody.length() > 100) {
					result.append(requestBody.substring(0, 100) + " ...");
				} else {
					result.append(requestBody);
				}
				result.append("\n");
			}
			// Response details
			result.append("Status:   ");
			result.append(httpResponse.getStatusLine() + "\n");
			// Response body
			if (httpResponse.getEntity() != null) {
				String responseBody = RestUtil.buildResponseBodyString(httpResponse);
				if (responseBody != null && responseBody.length() > 0) {
					if (responseBody.length() > 100) {
						responseBody = responseBody.substring(0, 100) + " ...";
					}
					result.append("Response: " + responseBody);
				}
			}
		} catch (Exception e) {
			throw new DocMakerException(e);
		}
		return result.toString();
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
	
	private static int nextTabId() {
		return tabIdSequence++;
	}

	public static String replacePlaceholder(String template, String placeholder, String replacement) {
		if (!template.contains(placeholder)) {
			throw new DocMakerException("Not able to find any placeholder: ${" + placeholder +"}");
		}
		return template.replace("${" + placeholder + "}", replacement);
	}

}
