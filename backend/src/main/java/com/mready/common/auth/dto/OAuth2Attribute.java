package com.mready.common.auth.dto;

import com.mready.common.error.ErrorCode;
import com.mready.common.error.exception.BusinessException;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OAuth2Attribute {
	public static final String KAKAO = "KAKAO";
	public static final String NAVER = "NAVER";

	private Map<String, Object> attributes;
	private String name;
	private String email;
	private String profileUrl;
	private String providerId;
	private String provider;
	private String nickname;
	private String phoneNumber;
	private String ageRange;
	private String birthday;
	private String birthYear;

	public static OAuth2Attribute of(final String registrationId, final Map<String, Object> attributes) {
		return switch (registrationId) {
			case KAKAO -> ofKakao(attributes);
			// 다른 소셜 로그인이 추가되면 여기에 로직 추가
			default -> throw new BusinessException(ErrorCode.INVALID_INPUT);
		};
	}

	private static OAuth2Attribute ofKakao(Map<String, Object> attributes) {
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

		String providerId = KAKAO + "_" + attributes.get("id");

        String name = (String) kakaoAccount.get("name");
		String email = (String) kakaoAccount.get("email");
		String nickname = (String) profile.get("nickname");
		String profileUrl = (String) profile.get("profile_image_url");

		String phoneNumber = (String) kakaoAccount.get("phone_number");
		String ageRange = (String) kakaoAccount.get("age_range");
		String birthday = (String) kakaoAccount.get("birthday");
		String birthYear = (String) kakaoAccount.get("birthyear");

		return OAuth2Attribute.builder()
			.name(name)
			.nickname(nickname)
			.email(email)
			.providerId(providerId)
			.profileUrl(profileUrl)
			.provider(KAKAO)
			.phoneNumber(phoneNumber)
			.ageRange(ageRange)
			.birthday(birthday)
			.birthYear(birthYear)
			.attributes(attributes)
			.build();
	}

	//todo : Member toEntity

}
