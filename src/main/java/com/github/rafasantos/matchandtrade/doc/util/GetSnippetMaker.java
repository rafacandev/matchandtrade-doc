package com.github.rafasantos.matchandtrade.doc.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import com.github.rafasantos.matchandtrade.doc.executable.PropertiesProvider;
import com.github.rafasantos.matchandtrade.doc.maker.rest.RestUtil;
import com.github.rafasantos.matchandtrade.exception.DocMakerException;

public class GetSnippetMaker {

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
		// Assert if status is 200
		AssertUtil.isEquals(httpStatus, httpResponse.getStatusLine().getStatusCode());
		
		return new RequestResponseHolder(httpRequest, httpResponse);
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
		// Assert if status is 204
		AssertUtil.isEquals(httpStatus, httpResponse.getStatusLine().getStatusCode());
		
		return new RequestResponseHolder(httpRequest, httpResponse);
	}
	
	public static RequestResponseHolder buildGetRequestResponse(String url) {
		List<Header> defaultHeaders = new ArrayList<>();
		defaultHeaders.add(RestUtil.getAuthenticationHeader());
		defaultHeaders.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
		return buildGetRequestResponse(url, defaultHeaders, HttpStatus.SC_OK);
	}

	
	public static RequestResponseHolder buildDeleteRequestResponse(String url) {
		List<Header> defaultHeaders = new ArrayList<>();
		defaultHeaders.add(RestUtil.getAuthenticationHeader());
		defaultHeaders.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
		return buildDeleteRequestResponse(url, defaultHeaders, HttpStatus.SC_NO_CONTENT);
	}
}
