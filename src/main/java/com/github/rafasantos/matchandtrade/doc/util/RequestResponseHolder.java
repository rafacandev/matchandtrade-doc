package com.github.rafasantos.matchandtrade.doc.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

public class RequestResponseHolder {
	private HttpRequestBase httpRequest;
	private HttpResponse httpResponse;

	public RequestResponseHolder(HttpRequestBase httpRequest, HttpResponse httpResponse) {
		this.httpRequest = httpRequest;
		this.httpResponse = httpResponse;
	}

	public HttpRequestBase getHttpRequest() {
		return httpRequest;
	}

	public void setHttpRequest(HttpRequestBase httpRequest) {
		this.httpRequest = httpRequest;
	}

	public HttpResponse getHttpResponse() {
		return httpResponse;
	}

	public void setHttpResponse(HttpResponse httpResponse) {
		this.httpResponse = httpResponse;
	}
	
}
