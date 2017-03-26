package com.github.rafasantos.matchandtrade.doc.maker.rest;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.github.rafasantos.matchandtrade.doc.executable.PropertiesProvider;
import com.github.rafasantos.matchandtrade.doc.maker.OutputMaker;


public class RestAuthenticationsMaker implements OutputMaker {
	
	public void positive() throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpRequest = new HttpGet(PropertiesProvider.getServerUrl() + "/authentications");
		CloseableHttpResponse httpResponse = httpClient.execute(httpRequest);
		
		Header[] requestHeaders = httpRequest.getAllHeaders();
		for (Header h : requestHeaders) {
			System.out.println(h.getName() + ": " + h.getValue());
		}
		
		Header[] responseHeaders = httpResponse.getAllHeaders();
		for (Header h : responseHeaders) {
			System.out.println(h.getName() + ": " + h.getValue());
		}
	}

	@Override
	public String obtainDocContent() {
		// TODO Auto-generated method stub
		return "bla-bla";
	}

	@Override
	public String getDocLocation() {
		return "doc/rest/authentications.md";
	}

}
