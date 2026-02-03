package com.mento.domain.mentor.converter;

import com.mento.domain.mentor.dto.common.MentoTypeInfoDto;
import com.mento.domain.mentor.entity.MentorType;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MentorTypeConverter {

	public MentoTypeInfoDto toMentorTypeInfoDto(final MentorType mentorType) {
		return MentoTypeInfoDto.builder()
			.id(mentorType.getId())
			.name(mentorType.getTypeName())
			.build();
	}
}