package com.mento.common.converter;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class JsonStringConverter {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * 쉼표로 구분된 문자열을 JSON 배열 문자열로 변환
	 * @param commaSeparated 쉼표로 구분된 문자열 (예: "건성, 지성")
	 * @return JSON 배열 문자열 (예: "[\"건성\",\"지성\"]")
	 */
	public static String toJsonArray(final String commaSeparated) {
		if (commaSeparated == null || commaSeparated.isBlank()) {
			return "[]";
		}

		try {
			List<String> items = Arrays.stream(commaSeparated.split(","))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.toList();
			
			return objectMapper.writeValueAsString(items);
		} catch (JsonProcessingException e) {
			log.error("Failed to convert comma-separated string to JSON array: {}", commaSeparated, e);
			return "[]";
		}
	}

	/**
	 * JSON 배열 문자열을 쉼표로 구분된 문자열로 변환
	 * @param jsonArray JSON 배열 문자열
	 * @return 쉼표로 구분된 문자열
	 */
	public static String fromJsonArray(final String jsonArray) {
		if (jsonArray == null || jsonArray.isBlank()) {
			return "";
		}

		try {
			List<String> items = objectMapper.readValue(jsonArray, 
				objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
			return String.join(", ", items);
		} catch (JsonProcessingException e) {
			log.error("Failed to convert JSON array to comma-separated string: {}", jsonArray, e);
			return "";
		}
	}
}
