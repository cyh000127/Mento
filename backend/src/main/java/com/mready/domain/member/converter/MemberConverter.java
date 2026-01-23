package com.mready.domain.member.converter;

import com.mready.common.auth.dto.OAuth2Attribute;
import com.mready.domain.member.dto.request.MemberCreateReqDto;
import com.mready.domain.member.dto.response.MemberResDto;
import com.mready.domain.member.entity.Member;
import com.mready.domain.member.entity.Role;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MemberConverter {

	public Member toEntity(final MemberCreateReqDto dto) {
		return Member.builder()
			.name(dto.name())
			.email(dto.email())
			.kakaoId("kakao_" + dto.email())
			.password("password")
			.role(Role.USER)
			.build();
	}

	public MemberResDto toMemberResDto(final Member entity) {
		return MemberResDto.builder()
			.id(entity.getId())
			.name(entity.getName())
			.email(entity.getEmail())
			.birthDate(entity.getBirthDate())
			.createdAt(entity.getCreatedAt())
			.updatedAt(entity.getUpdatedAt())
			.build();
	}

	public Member toEntity(final OAuth2Attribute attribute) {
		String password = String.valueOf(attribute.getKakaoId().hashCode());
		
		return Member.builder()
				.name(attribute.getName())
				.email(attribute.getEmail())
				.kakaoId(attribute.getKakaoId())
				.password(password)
				.birthDate(attribute.getBirthDate())
				.role(Role.USER)
				.build();
	}
}
