package com.github.rafasantos.matchandtrade.doc.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;

import com.github.rafasantos.matchandtrade.doc.executable.PropertiesProvider;
import com.github.rafasantos.matchandtrade.doc.maker.rest.RestAuthenticateMaker;
import com.github.rafasantos.matchandtrade.exception.DocMakerException;
import com.matchandtrade.rest.v1.json.AuthenticationJson;
import com.matchandtrade.rest.v1.json.UserJson;

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
			RequestResponseHolder requestResponseHolder = authenticate.buildAuthenticateRequestResponse();
			authenticationHeader = getAuthenticationHeaderFromResponse(requestResponseHolder.getHttpResponse());
		}
		return authenticationHeader;
	}
	
	public static void setAuthenticationHeader(Header header) {
		authenticationHeader = header;
	}
	
	public static UserJson getAuthenticatedUser() {
		HttpClient httpClient = HttpClients.createDefault();
		HttpGet httpRequest = new HttpGet(PropertiesProvider.getServerUrl() + "/rest/v1/authentications/");
		httpRequest.addHeader(RestUtil.getAuthenticationHeader());
		httpRequest.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(httpRequest);
			String responseBody = buildResponseBodyString(httpResponse);
			AuthenticationJson authenticationJson = JsonUtil.fromString(responseBody, AuthenticationJson.class);
			HttpGet httpRequestUser = new HttpGet(PropertiesProvider.getServerUrl() + "/rest/v1/users/" + authenticationJson.getUserId());
			httpRequestUser.addHeader(RestUtil.getAuthenticationHeader());
			httpRequestUser.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
			HttpResponse httpResponseUser = httpClient.execute(httpRequestUser);
			String userResponseBody = IOUtils.toString(httpResponseUser.getEntity().getContent(), StandardCharsets.UTF_8);
			UserJson result = JsonUtil.fromString(userResponseBody, UserJson.class);
			
			return result;
		} catch (IOException e) {
			throw new DocMakerException(e);
		}
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
}
