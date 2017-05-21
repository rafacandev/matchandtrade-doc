package com.matchandtrade.doc.util;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

public class RequestResponseHolder {
	private HttpRequestBase httpRequest;
	private HttpResponse httpResponse;
	private Header authorizationHeader;
	
	public RequestResponseHolder(HttpRequestBase httpRequest, HttpResponse httpResponse) {
		this.httpRequest = httpRequest;
		this.httpResponse = httpResponse;
	}

	public Header getAuthorizationHeader() {
		return authorizationHeader;
	}

	public HttpRequestBase getHttpRequest() {
		return httpRequest;
	}

	public HttpResponse getHttpResponse() {
		return httpResponse;
	}

	public void setAuthorizationHeader(Header authorizationHeader) {
		this.authorizationHeader = authorizationHeader;
	}

	public void setHttpRequest(HttpRequestBase httpRequest) {
		this.httpRequest = httpRequest;
	}

	public void setHttpResponse(HttpResponse httpResponse) {
		this.httpResponse = httpResponse;
	}
	
}
