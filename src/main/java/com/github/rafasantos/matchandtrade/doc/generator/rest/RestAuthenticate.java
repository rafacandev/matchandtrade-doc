package com.github.rafasantos.matchandtrade.doc.generator.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.github.rafasantos.matchandtrade.doc.executable.PropertiesProvider;
import com.github.rafasantos.matchandtrade.doc.generator.OutputGenerator;
import com.matchandtrade.config.AuthenticationProperties;


public class RestAuthenticate implements OutputGenerator {
	
	private static final String AUTHENTICATE_POSITIVE_REQUEST = "AUTHENTICATE_POSITIVE_REQUEST";
	private static final String AUTHENTICATE_POSITIVE_RESPONSE = "AUTHENTICATE_POSITIVE_RESPONSE";
//	private static final String AUTHENTICATE_NEGATIVE_REQUEST = "AUTHENTICATE_NEGATIVE_REQUEST";
//	private static final String AUTHENTICATE_NEGATIVE_RESPONSE = "AUTHENTICATE_NEGATIVE_RESPONSE";
	
	private StringBuilder positiveRequestOuput = new StringBuilder();;
	private StringBuilder positiveResponseOuput = new StringBuilder();
//	private StringBuilder negativeRequestOuput = new StringBuilder();
//	private StringBuilder negativeResponseOuput = new StringBuilder();

	
	public void execute() {
		try {
			positive();
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void positive() throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpRequest = new HttpGet(PropertiesProvider.getServerUrl() + "/authenticate");
		CloseableHttpResponse httpResponse = httpClient.execute(httpRequest);
		
		StringBuilder headers = new StringBuilder();
		for (Header h : httpResponse.getAllHeaders()) {
			headers.append(h.getName() + ": ");
			headers.append(h.getValue());
		}
		
		assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
		assertTrue(headers.toString().contains("Authorization"));
		
		positiveRequestOuput.append(httpRequest.getMethod() + " " + httpRequest.getURI());
		
		
		positiveResponseOuput.append(httpResponse.getStatusLine().getProtocolVersion() + " ");
		positiveResponseOuput.append(httpResponse.getStatusLine().getStatusCode() + " ");
		positiveResponseOuput.append(Response.Status.fromStatusCode(httpResponse.getStatusLine().getStatusCode()).getReasonPhrase());
		Header[] authoHeaders = httpResponse.getHeaders(AuthenticationProperties.OAuth.AUTHORIZATION_HEADER.toString());
		positiveResponseOuput.append("\nHeaders: ");
		for (int i = 0; i < authoHeaders.length; i++) {
			positiveResponseOuput.append("\n\t" + authoHeaders[i].getName() + ": ");
			positiveResponseOuput.append(authoHeaders[i].getValue());
		}
		positiveResponseOuput.append("\n\n");
		String responseBody = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
		positiveResponseOuput.append(responseBody);
	}

	@Override
	public String getDocOutput() {
		try {
			return buildDocOutput();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String buildDocOutput() throws IOException {
		String authenticationsPath = this.getClass().getClassLoader().getResource("doc/rest/resources/authenticate.md").getFile();
		File authenticationsFile = new File(authenticationsPath);
		String authenticationsString = FileUtils.readFileToString(authenticationsFile, StandardCharsets.UTF_8);
		authenticationsString = authenticationsString.replace("${" + AUTHENTICATE_POSITIVE_REQUEST + "}", positiveRequestOuput);
		authenticationsString = authenticationsString.replace("${" + AUTHENTICATE_POSITIVE_RESPONSE + "}", positiveResponseOuput);
		return authenticationsString;
	}

	@Override
	public String getDocOutputLocation() {
		return "rest/resources/authenticate.md";
	}
}
