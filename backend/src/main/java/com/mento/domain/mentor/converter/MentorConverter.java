package com.mento.domain.mentor.converter;

import com.mento.domain.mentor.dto.common.MentoTypeInfoDto;
import com.mento.domain.mentor.dto.common.MentorInfoDto;
import com.mento.domain.mentor.entity.Mentor;
import com.mento.domain.mentor.entity.MentorType;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MentorConverter {

	public MentorInfoDto toMentorInfoDto(final Mentor mentor) {
		return MentorInfoDto.builder()
			.id(mentor.getId())
			.name(mentor.getName())
			.build();
	}

	public MentoTypeInfoDto toMentorTypeInfoDto(final MentorType mentorType) {
		return MentoTypeInfoDto.builder()
			.id(mentorType.getId())
			.name(mentorType.getTypeName())
			.build();
	}
}