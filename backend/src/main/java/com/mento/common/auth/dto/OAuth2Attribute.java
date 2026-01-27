package com.mento.common.auth.dto;

import java.time.LocalDate;
import java.util.Map;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
public class OAuth2Attribute {
	public static final String KAKAO = "kakao";

	private Map<String, Object> attributes;
	private String name;
	private String email;
	private String kakaoId;
	private LocalDate birthDate;

	public static OAuth2Attribute of(final String registrationId, final Map<String, Object> attributes) {
		if (KAKAO.equals(registrationId)) {
			return ofKakao(attributes);
		}
		throw new BusinessException(ErrorCode.INVALID_INPUT);
	}

	private static OAuth2Attribute ofKakao(Map<String, Object> attributes) {
		log.info("Kakao Attributes: {}", attributes);
		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
		
		if (kakaoAccount == null) {
			throw new BusinessException(ErrorCode.INVALID_INPUT);
		}

		Map<String, Object> profile = (Map<String, Object>)kakaoAccount.get("profile");

		String kakaoId = KAKAO + "_" + attributes.get("id");

		if (profile == null || profile.get("nickname") == null) {
			throw new BusinessException(ErrorCode.INVALID_INPUT);
		}
		String name = (String)profile.get("nickname");
		String email = (String)kakaoAccount.get("email");

		return OAuth2Attribute.builder()
			.name(name)
			.email(email)
			.kakaoId(kakaoId)
			.birthDate(null)
			.attributes(attributes)
			.build();
	}

}
