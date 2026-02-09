package com.mento.domain.user.converter;

import com.mento.common.auth.dto.OAuth2Attribute;
import com.mento.domain.user.dto.common.UserInfoDto;
import com.mento.domain.user.dto.response.UserResDto;
import com.mento.domain.user.entity.Role;
import com.mento.domain.user.entity.User;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserConverter {

	public UserResDto toUserResDto(final User entity) {
		return UserResDto.builder()
			.id(entity.getId())
			.name(entity.getName())
			.email(entity.getEmail())
			.birthDate(entity.getBirthDate())
			.role(entity.getRole())
			.createdAt(entity.getCreatedAt())
			.updatedAt(entity.getUpdatedAt())
			.build();
	}

	public UserInfoDto toUserInfoDto(final User user) {
		return new UserInfoDto(user.getId(), user.getName());
	}

	public User toEntity(final OAuth2Attribute attribute) {
		String password = String.valueOf(attribute.getKakaoId().hashCode());

		return User.builder()
			.name(attribute.getName())
			.email(attribute.getEmail())
			.kakaoId(attribute.getKakaoId())
			.password(password)
			.birthDate(attribute.getBirthDate())
			.role(Role.USER)
			.build();
	}
}
