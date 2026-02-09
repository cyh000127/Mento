package com.mento.domain.reservation.vo;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mento.domain.reservation.dto.common.SurveyQuestionAnswerDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SurveyData {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final List<SurveyQuestionAnswerDto> surveyList;

	public static SurveyData from(final String jsonString) {
		if (jsonString == null || jsonString.isBlank()) {
			return new SurveyData(Collections.emptyList());
		}

		try {
			List<SurveyQuestionAnswerDto> parsedData = OBJECT_MAPPER.readValue(
				jsonString,
				new TypeReference<>() {
				}
			);
			return new SurveyData(parsedData);
		} catch (JsonProcessingException _) {
			return new SurveyData(Collections.emptyList());
		}
	}

	public static SurveyData from(final List<SurveyQuestionAnswerDto> surveyList) {
		if (surveyList == null || surveyList.isEmpty()) {
			return new SurveyData(Collections.emptyList());
		}
		return new SurveyData(List.copyOf(surveyList));
	}

	public List<SurveyQuestionAnswerDto> getSurveyList() {
		return Collections.unmodifiableList(surveyList);
	}

	public boolean isEmpty() {
		return surveyList.isEmpty();
	}
}
