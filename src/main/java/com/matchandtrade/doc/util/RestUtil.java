package com.matchandtrade.doc.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;

import com.matchandtrade.doc.maker.rest.RestAuthenticationMaker;
import com.matchandtrade.doc.maker.rest.RestUserMaker;
import com.matchandtrade.exception.DocMakerException;
import com.matchandtrade.rest.v1.json.AuthenticationJson;
import com.matchandtrade.rest.v1.json.UserJson;

public class RestUtil {

	private static Header authenticationHeader = null;
	
	// Utility classes, which are a collection of static members, are not meant to be instantiated.
	private RestUtil() {}

	public static String buildResponseBodyString(HttpResponse httpResponse) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			httpResponse.getEntity().writeTo(outputStream);
			
			// Creating a new entity so it can be read again
			ByteArrayEntity entity = new ByteArrayEntity(outputStream.toByteArray(), ContentType.get(httpResponse.getEntity()));
			httpResponse.setEntity(entity);
			
			return IOUtils.toString(outputStream.toByteArray(), StandardCharsets.UTF_8.toString());
		} catch (UnsupportedOperationException | IOException e) {
			throw new DocMakerException("Error building response body as string", e);
		}
	}

	/**
	 * Get the authenticated user in JSON format.
	 * @return userJson for the authenticated user
	 */
	public static UserJson getAuthenticatedUser() {
		RequestResponseHolder authentication = RequestResponseUtil.buildGetRequestResponse(RestAuthenticationMaker.BASE_URL);
		String authenticationResponse = buildResponseBodyString(authentication.getHttpResponse());
		AuthenticationJson authenticationJson = JsonUtil.fromString(authenticationResponse, AuthenticationJson.class);
		RequestResponseHolder getUser = RequestResponseUtil.buildGetRequestResponse(RestUserMaker.BASE_URL + authenticationJson.getUserId());
		String responseBody = buildResponseBodyString(getUser.getHttpResponse());
		return JsonUtil.fromString(responseBody, UserJson.class);
	}
	
	/**
	 * Get an Authentication header to be used in secured requests.
	 * The header is generated once and reused.
	 * 
	 * @return an authenticated header
	 */
	public static Header getAuthenticationHeader() {
		if (authenticationHeader == null) {
			RequestResponseHolder requestResponseHolder = RequestResponseUtil.buildAuthenticateRequestResponse();
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

	public static void setAuthenticationHeader(Header header) {
		authenticationHeader = header;
	}

}
