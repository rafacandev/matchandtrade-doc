package com.matchandtrade.doc.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;

import com.matchandtrade.doc.executable.PropertiesProvider;
import com.matchandtrade.exception.DocMakerException;
import com.matchandtrade.rest.Json;
import com.matchandtrade.rest.JsonLinkSupport;

public class RequestResponseUtil {

	private static final String BASE_URL = "/authenticate";

	public enum MethodType {POST, PUT}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestResponseUtil.class);
	
	
	// Utility classes should not have public constructors
	private RequestResponseUtil() {}
	
	/**
	 * Build a RequestResponseHolder from GET /authenticate.
	 * @return RequestResponseHolder for authenticate
	 */
	public static RequestResponseHolder buildAuthenticateRequestResponse() {
		RequestResponseHolder result = RequestResponseUtil.buildGetRequestResponse(BASE_URL, new ArrayList<Header>(), HttpStatus.SC_OK);

		// Assert if contains Authorization header
		Set<String> headers = new HashSet<>();
		for (Header h : result.getHttpResponse().getAllHeaders()) {
			headers.add(h.getName());
		}
		AssertUtil.isTrue(headers.toString().contains("Authorization"));
		result.setAuthorizationHeader(RestUtil.buildAuthorizationHeaderFromResponse(result.getHttpResponse()));
		return result;
	}
	
	private static List<Header> buildDefaultHeaders() {
		List<Header> defaultHeaders = new ArrayList<>();
		defaultHeaders.add(RestUtil.getAuthorizationHeader());
		defaultHeaders.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
		return defaultHeaders;
	}
	
	public static RequestResponseHolder buildDeleteRequestResponse(String url) {
		List<Header> defaultHeaders = buildDefaultHeaders();
		return buildDeleteRequestResponse(url, defaultHeaders, HttpStatus.SC_NO_CONTENT);
	}

	public static RequestResponseHolder buildDeleteRequestResponse(String url, List<Header> headers, int httpStatus) {
		HttpClient httpClient = HttpClients.createDefault();
		HttpDelete httpRequest = new HttpDelete(PropertiesProvider.getServerUrl() + url);
		for (Header h : headers) {
			httpRequest.addHeader(h);
		}
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(httpRequest);
		} catch (IOException e) {
			throw new DocMakerException(e);
		}
		assertStatusCode(httpStatus, httpRequest, httpResponse);		
		return new RequestResponseHolder(httpRequest, httpResponse);
	}

	public static RequestResponseHolder buildGetRequestResponse(String url) {
		List<Header> defaultHeaders = buildDefaultHeaders();
		return buildGetRequestResponse(url, defaultHeaders, HttpStatus.SC_OK);
	}

	public static RequestResponseHolder buildGetRequestResponse(String url, List<Header> headers, int httpStatus) {
		HttpClient httpClient = HttpClients.createDefault();
		HttpGet httpRequest = new HttpGet(PropertiesProvider.getServerUrl() + url);
		for (Header h : headers) {
			httpRequest.addHeader(h);
		}
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(httpRequest);
		} catch (IOException e) {
			throw new DocMakerException(e);
		}
		assertStatusCode(httpStatus, httpRequest, httpResponse);
		return new RequestResponseHolder(httpRequest, httpResponse);
	}

	private static void assertStatusCode(int httpStatus, HttpRequestBase httpRequest, HttpResponse httpResponse) {
		String snippet = TemplateUtil.buildSnippet(httpRequest, httpResponse);
		LOGGER.debug(snippet);
		if (httpResponse.getStatusLine().getStatusCode() != httpStatus) {
			if (!LOGGER.isDebugEnabled()) {
				LOGGER.info("RequesResponse assetion failed: \n" + snippet);
			}
			throw new DocMakerException("Expected [" + httpStatus + "] but found [" + httpResponse.getStatusLine().getStatusCode() + "].");
		}
	}

	public static RequestResponseHolder buildPostRequestResponse(String url, Json body) {
		List<Header> defaultHeaders = buildDefaultHeaders();
		return buildPutOrPostRequestResponse(url, body, defaultHeaders, HttpStatus.SC_CREATED, MethodType.POST);
	}
	
	public static RequestResponseHolder buildPutOrPostRequestResponse(String url, Json body, List<Header> headers, int httpStatus, MethodType methodType) {
		HttpClient httpClient = HttpClients.createDefault();
		HttpEntityEnclosingRequestBase httpRequest = new HttpPut(PropertiesProvider.getServerUrl() + url);
		if (methodType == MethodType.POST) {
			httpRequest = new HttpPost(PropertiesProvider.getServerUrl() + url);
		} else {
			httpRequest = new HttpPut(PropertiesProvider.getServerUrl() + url);
		}
		for (Header h : headers) {
			httpRequest.addHeader(h);
		}
		
		// Remove links (HATEOAS) from the body when doing POST or PUT
		if (body instanceof JsonLinkSupport) {
			JsonLinkSupport jls = (JsonLinkSupport) body;
			jls.setLinks(new HashSet<Link>());
		}
		
		StringEntity requestBody = new StringEntity(JsonUtil.toJson(body), StandardCharsets.UTF_8);
		httpRequest.setEntity(requestBody);
		
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(httpRequest);
		} catch (IOException e) {
			throw new DocMakerException(e);
		}

		assertStatusCode(httpStatus, httpRequest, httpResponse);
		
		return new RequestResponseHolder(httpRequest, httpResponse);
	}

	public static RequestResponseHolder buildPutRequestResponse(String url, Json body) {
		List<Header> defaultHeaders = buildDefaultHeaders();
		return buildPutOrPostRequestResponse(url, body, defaultHeaders, HttpStatus.SC_OK, MethodType.PUT);
	}
	
}
