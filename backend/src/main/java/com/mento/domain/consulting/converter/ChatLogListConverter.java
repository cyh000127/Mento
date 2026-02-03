package com.mento.domain.consulting.converter;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mento.domain.consulting.vo.ChatLogEntryVo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ChatLogListConverter implements AttributeConverter<List<ChatLogEntryVo>, String> {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(List<ChatLogEntryVo> attribute) {

		try {
			return objectMapper.writeValueAsString(attribute);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("JSON 변환 실패", e);
		}
	}

	@Override
	public List<ChatLogEntryVo> convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.isEmpty()) {
			return List.of();
		}
		try {
			return objectMapper.readValue(dbData, new TypeReference<>() {
			});
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("JSON 파싱 실패", e);
		}
	}
}