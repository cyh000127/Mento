package com.mento.domain.product.converter;

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
	 * List<String>을 JSON 문자열로 변환
	 * @param list 변환할 리스트
	 * @return JSON 문자열 (예: "[\"지성\", \"민감성\"]")
	 */
	public static String toJsonString(final List<String> list) {
		if (list == null || list.isEmpty()) {
			return "[]";
		}

		try {
			return objectMapper.writeValueAsString(list);
		} catch (JsonProcessingException e) {
			log.error("Failed to convert list to JSON string: {}", list, e);
			return "[]";
		}
	}

	/**
	 * JSON 문자열을 List<String>으로 변환
	 * @param jsonString JSON 문자열
	 * @return List<String>
	 */
	public static List<String> toList(final String jsonString) {
		if (jsonString == null || jsonString.isBlank()) {
			return List.of();
		}

		try {
			return objectMapper.readValue(jsonString, 
				objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
		} catch (JsonProcessingException e) {
			log.error("Failed to convert JSON string to list: {}", jsonString, e);
			return List.of();
		}
	}
}
