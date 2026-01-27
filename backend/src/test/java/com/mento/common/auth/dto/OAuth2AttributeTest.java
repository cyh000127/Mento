package com.mento.common.auth.dto;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("OAuth2Attribute 단위 테스트")
class OAuth2AttributeTest {

	@Test
	@DisplayName("OAuth2Attribute_변환_확인")
	void 카카오_OAuth2Attribute_변환_확인() {
		// given
		Map<String, Object> attributes = new HashMap<>();
		Map<String, Object> kakaoAccount = new HashMap<>();
		Map<String, Object> profile = new HashMap<>();

		attributes.put("id", 123456789L);

		kakaoAccount.put("email", "gildong@example.com");

		profile.put("nickname", "홍길동");
		kakaoAccount.put("profile", profile);

		attributes.put("kakao_account", kakaoAccount);

		// when
		OAuth2Attribute result = OAuth2Attribute.of(OAuth2Attribute.KAKAO, attributes);

		// then
		assertThat(result.getKakaoId()).isEqualTo("kakao_123456789");
		assertThat(result.getName()).isEqualTo("홍길동");
		assertThat(result.getEmail()).isEqualTo("gildong@example.com");
		assertThat(result.getBirthDate()).isNull();
	}

	@Test
	@DisplayName("OAuth2Attribute_KakaoAccount_Missing_Exception_확인")
	void 카카오_KakaoAccount_Missing_Exception_확인() {
		// given
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("id", 123456789L);
		// kakao_account missing
		attributes.put("kakao_account", null);

		// when & then
		assertThatThrownBy(() -> OAuth2Attribute.of(OAuth2Attribute.KAKAO, attributes))
			.isInstanceOf(com.mento.common.error.exception.BusinessException.class)
			.hasFieldOrPropertyWithValue("errorCode", com.mento.common.error.ErrorCode.INVALID_INPUT);
	}

	@Test
	@DisplayName("OAuth2Attribute_Name_Missing_Exception_확인")
	void 카카오_Name_Missing_Exception_확인() {
		// given
		Map<String, Object> attributes = new HashMap<>();
		Map<String, Object> kakaoAccount = new HashMap<>();

		attributes.put("id", 123456789L);
		kakaoAccount.put("email", "test@example.com");
		// profile or nickname missing
		kakaoAccount.put("profile", null);

		attributes.put("kakao_account", kakaoAccount);

		// when & then
		assertThatThrownBy(() -> OAuth2Attribute.of(OAuth2Attribute.KAKAO, attributes))
			.isInstanceOf(com.mento.common.error.exception.BusinessException.class)
			.hasFieldOrPropertyWithValue("errorCode", com.mento.common.error.ErrorCode.INVALID_INPUT);
	}
}
