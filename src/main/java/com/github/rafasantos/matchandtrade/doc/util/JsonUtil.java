package com.github.rafasantos.matchandtrade.doc.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.rafasantos.matchandtrade.exception.DocMakerException;

/**
 * Utility class for common JSON manipulations
 * 
 * @author rafael.santos.bra@gmail.com
 *
 */
public class JsonUtil {

	private static ObjectMapper objectMapper;

	private static void init() {
		objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);		
	}
	
	/**
	 * Prettyfi a JSON string
	 * 
	 * @param json
	 * @return pretty json
	 */
	public static String prettyJson(String json) {
		if (objectMapper == null) {
			init();
		}
		String result = null;
		try {
			Object jsonObject = objectMapper.readValue(json, Object.class);
			result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
		} catch (IOException e) {
			throw new DocMakerException("Not able to parse string to JSON: " + json, e);
		}
		return result;
	}

}
