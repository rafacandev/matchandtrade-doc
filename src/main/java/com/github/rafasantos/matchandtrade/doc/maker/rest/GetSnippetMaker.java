package com.github.rafasantos.matchandtrade.doc.maker.rest;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import com.github.rafasantos.matchandtrade.doc.executable.PropertiesProvider;
import com.github.rafasantos.matchandtrade.doc.util.AssertUtil;
import com.github.rafasantos.matchandtrade.doc.util.RequestResponseHolder;
import com.github.rafasantos.matchandtrade.exception.DocMakerException;

public class GetSnippetMaker {

	public static RequestResponseHolder buildGetRequestResponse(String url) {
		HttpClient httpClient = HttpClients.createDefault();
		HttpGet httpRequest = new HttpGet(PropertiesProvider.getServerUrl() + url);
		httpRequest.addHeader(RestUtil.getAuthenticationHeader());
		httpRequest.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(httpRequest);
		} catch (IOException e) {
			throw new DocMakerException(e);
		}
		// Assert if status is 200
		AssertUtil.isEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
		
		return new RequestResponseHolder(httpRequest, httpResponse);
	}
	
}
