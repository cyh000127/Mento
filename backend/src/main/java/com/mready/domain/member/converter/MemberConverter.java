package com.mready.domain.member.converter;

import com.mready.domain.member.dto.request.MemberCreateReqDto;
import com.mready.domain.member.dto.response.MemberResDto;
import com.mready.domain.member.entity.Member;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MemberConverter {

	public Member toEntity(final MemberCreateReqDto dto) {
		return Member.builder()
			.name(dto.name())
			.email(dto.email())
			.build();
	}

	public MemberResDto toMemberResDto(final Member entity) {
		return MemberResDto.builder()
			.id(entity.getId())
			.name(entity.getName())
			.email(entity.getEmail())
			.createdAt(entity.getCreatedAt())
			.updatedAt(entity.getUpdatedAt())
			.build();
	}
}
