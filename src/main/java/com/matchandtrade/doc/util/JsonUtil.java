package com.matchandtrade.doc.util;

import java.io.IOException;

import org.apache.http.HttpResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.matchandtrade.exception.DocMakerException;
import com.matchandtrade.rest.v1.json.TradeJson;

import io.restassured.response.Response;

/**
 * Utility class for common JSON manipulations
 * 
 * @author rafael.santos.bra@gmail.com
 *
 */
public class JsonUtil {
	
	private static ObjectMapper objectMapper;

	// Utility classes, which are a collection of static members, are not meant to be instantiated.
	private JsonUtil() { }

	/**
	 * Parse an JSON Object from a <code>Response</code>.
	 * @param httpResponse
	 * @return target json class
	 */
	public static TradeJson fromResponse(Response response, Class<TradeJson> type) {
		try {
			return fromString(response.body().asString(), type);
		} catch (Exception e) {
			throw new DocMakerException("Not able to parse from HttpResponse to Json object." + e);
		}
	}
	
	/**
	 * Parse an JSON Object from a <code>HttpResponse</code>.
	 * @param httpResponse
	 * @return target json class
	 */
	public static <T> T fromHttpResponse(HttpResponse httpResponse, Class<T> type ) {
		try {
			String response = RestUtil.buildResponseBodyString(httpResponse);
			return fromString(response, type);
		} catch (Exception e) {
			throw new DocMakerException("Not able to parse from HttpResponse to Json object." + e);
		}
	}
	
	/**
	 * Parse an JSON Object from a string.
	 * @param o
	 * @return JSON string
	 */
	public static <T> T fromString(String string, Class<T> type ) {
		try {
			return getObjectMapper().readValue(string, type);
		} catch (Exception e) {
			throw new DocMakerException("Not able to parse from string to Json object. String value: " + string, e);
		}
	}

	/**
	 * Instantiate objectMapper with default configuration if it is null, then, return objectMapper. 
	 * @return objectMapper with default config
	 */
	private static ObjectMapper getObjectMapper() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
			objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);		
		}
		return objectMapper;
	}
	
	/**
	 * Attempt to prettyfi a JSON string. If error (e.g: the string is not a valid json, 
	 * it returns the original string.
	 * 
	 * @param json
	 * @return pretty json
	 */
	public static String prettyJson(String json) {
		String result = null;
		try {
			Object jsonObject = getObjectMapper().readValue(json, Object.class);
			result = getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
			return result;
		} catch (IOException e) {
			return json;
		}
	}


	/**
	 * Parse an object to a JSON string.
	 * @param o
	 * @return JSON string
	 */
	public static String toJson(Object o) {
		try {
			return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o);
		} catch (JsonProcessingException e) {
			throw new DocMakerException("Not able to parse object to string", e);
		}
	}

}
