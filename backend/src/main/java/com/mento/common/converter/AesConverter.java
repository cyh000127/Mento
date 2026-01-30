package com.mento.common.converter;

import com.mento.common.util.AesUtils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

@Converter
@RequiredArgsConstructor
public class AesConverter implements AttributeConverter<String, String> {

	private final AesUtils aesUtils;

	@Override
	public String convertToDatabaseColumn(String attribute) {
		if (attribute == null) {
			return null;
		}
		return aesUtils.encrypt(attribute);
	}

	@Override
	public String convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		}
		return aesUtils.decrypt(dbData);
	}
}
