package com.mready.common.auth.dto;

import com.mready.common.error.ErrorCode;
import com.mready.common.error.exception.BusinessException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

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
		return switch (registrationId) {
			case KAKAO -> ofKakao(attributes);
			// 다른 소셜 로그인이 추가되면 여기에 로직 추가
			default -> throw new BusinessException(ErrorCode.INVALID_INPUT);
		};
	}

	private static OAuth2Attribute ofKakao(Map<String, Object> attributes) {
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
//		Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile"); //  Profile에서는 닉네임 / 프로필 사진만 가져옴

		String kakaoId = KAKAO + "_" + attributes.get("id");

		String name = (String) kakaoAccount.get("name");
		String email = (String) kakaoAccount.get("email");

		String birthday = (String) kakaoAccount.get("birthday");
		String birthYear = (String) kakaoAccount.get("birthyear");

		LocalDate birthDate = null;

		if (birthYear != null && birthday != null) {
			String dateStr = birthYear + birthday;
			try {
				birthDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
			} catch (DateTimeParseException _) {
                throw new BusinessException(ErrorCode.INVALID_INPUT);
			}
		}

		return OAuth2Attribute.builder()
			.name(name)
			.email(email)
			.kakaoId(kakaoId)
			.birthDate(birthDate)
			.attributes(attributes)
			.build();
	}

}
