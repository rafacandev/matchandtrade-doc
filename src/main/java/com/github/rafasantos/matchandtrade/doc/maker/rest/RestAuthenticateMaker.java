package com.github.rafasantos.matchandtrade.doc.maker.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.github.rafasantos.matchandtrade.doc.executable.PropertiesProvider;
import com.github.rafasantos.matchandtrade.doc.maker.OutputMaker;
import com.github.rafasantos.matchandtrade.doc.util.AssertUtil;
import com.github.rafasantos.matchandtrade.doc.util.TemplateUtil;
import com.github.rafasantos.matchandtrade.exception.DocMakerException;
import com.matchandtrade.config.AuthenticationProperties;


public class RestAuthenticateMaker implements OutputMaker {
	
	public static final String AUTHENTICATE_POSITIVE_REQUEST_PLACEHOLDER = "AUTHENTICATE_POSITIVE_REQUEST";
	public static final String AUTHENTICATE_POSITIVE_RESPONSE_PLACEHOLDER = "AUTHENTICATE_POSITIVE_RESPONSE";

	private CloseableHttpClient httpClient;
	private HttpGet httpRequest;
	private CloseableHttpResponse httpResponse;
	
	public RestAuthenticateMaker() {
		httpClient = HttpClients.createDefault();
		httpRequest = new HttpGet(PropertiesProvider.getServerUrl() + "/authenticate");
		try {
			httpResponse = httpClient.execute(httpRequest);
		} catch (Exception e) {
			throw new DocMakerException(this, e);
		}
	}
	
	public String buildAuthenticateRequestOutputPositive() {
		StringBuilder headers = new StringBuilder();
		for (Header h : httpResponse.getAllHeaders()) {
			headers.append(h.getName() + ": ");
			headers.append(h.getValue());
		}
		AssertUtil.areEqual(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
		AssertUtil.isTrue(headers.toString().contains("Authorization"));
		StringBuilder result = new StringBuilder();;
		result.append(httpRequest.getMethod() + " " + httpRequest.getURI());
		return result.toString();
	}
	
	public String buildAuthenticateResponseOutputPositive() {
		StringBuilder result = new StringBuilder();
		result.append(httpResponse.getStatusLine().getProtocolVersion() + " ");
		result.append(httpResponse.getStatusLine().getStatusCode() + " ");
		result.append(Response.Status.fromStatusCode(httpResponse.getStatusLine().getStatusCode()).getReasonPhrase());
		Header[] authoHeaders = httpResponse.getHeaders(AuthenticationProperties.OAuth.AUTHORIZATION_HEADER.toString());
		result.append("\nHeaders: ");
		for (int i = 0; i < authoHeaders.length; i++) {
			result.append("\n\t" + authoHeaders[i].getName() + ": ");
			result.append(authoHeaders[i].getValue());
		}
		result.append("\n\n");
		String responseBody;
		try {
			responseBody = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new DocMakerException(this, e);
		}
		result.append(responseBody);
		return result.toString();
	}

	private String buildDocOutput() throws IOException {
		String template = TemplateUtil.buildTemplate("doc/rest/resources/authenticate.md");
		String positiveRequestOuput = buildAuthenticateRequestOutputPositive();
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATE_POSITIVE_REQUEST_PLACEHOLDER, positiveRequestOuput);
		String positiveResponseOuput = buildAuthenticateResponseOutputPositive();
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATE_POSITIVE_RESPONSE_PLACEHOLDER, positiveResponseOuput);
		return template;
	}

	public void execute() {
		
	}
	
	@Override
	public String obtainDocOutput() {
		try {
			buildAuthenticateRequestOutputPositive();
			return buildDocOutput();
		} catch (IOException e) {
			throw new DocMakerException(this, e);
		}
	}

	@Override
	public String obtainDocOutputLocation() {
		return "rest/resources/authenticate.md";
	}
}
