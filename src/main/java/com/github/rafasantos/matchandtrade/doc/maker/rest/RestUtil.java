package com.github.rafasantos.matchandtrade.doc.maker.rest;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import com.github.rafasantos.matchandtrade.doc.util.RequestResponseHolder;

public class RestUtil {

	private static Header authenticationHeader = null;
	
	/**
	 * Gets an Authentication header to be used in secured requests.
	 * The header is generated once and reused.
	 * 
	 * @return an authenticated header
	 */
	public static Header getAuthenticationHeader() {
		if (authenticationHeader == null) {
			RestAuthenticateMaker authenticate = new RestAuthenticateMaker();
			RequestResponseHolder requestResponseHolder = authenticate.testPositive();
			authenticationHeader = getAuthenticationHeaderFromResponse(requestResponseHolder.getHttpResponse());
		}
		return authenticationHeader;
	}

	private static Header getAuthenticationHeaderFromResponse(HttpResponse httpResponse) {
		Header result = null;
		for (Header h : httpResponse.getAllHeaders()) {
			if (h.getName().equals("Authorization")) {
				result = h;
				break;
			}
		}
		return result;
	}
}
