package com.github.rafasantos.matchandtrade.doc.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.rafasantos.matchandtrade.exception.DocMakerException;

public class JsonUtil {


	public static String prettyJson(String responseBody) {
		ObjectMapper objectMapper;
		objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);		
		
		String result = null;
		try {
			Object json = objectMapper.readValue(responseBody, Object.class);
			result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		} catch (IOException e) {
			throw new DocMakerException("Not able to parse string to JSON: " + responseBody, e);
		}
		return result;
	}
}
