package com.mready.common.auth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OAuth2Attribute 단위 테스트")
class OAuth2AttributeTest {

	@Test
	@DisplayName("OAuth2Attribute_변환_확인")
	void 카카오_OAuth2Attribute_변환_확인() {
		// given
		Map<String, Object> attributes = new HashMap<>();
		Map<String, Object> kakaoAccount = new HashMap<>();

		attributes.put("id", 123456789L);

		kakaoAccount.put("email", "gildong@example.com");
		kakaoAccount.put("name", "홍길동");
		
		attributes.put("kakao_account", kakaoAccount);

		// when
		OAuth2Attribute result = OAuth2Attribute.of(OAuth2Attribute.KAKAO, attributes);

		// then
		assertThat(result.getKakaoId()).isEqualTo("kakao_123456789");
		assertThat(result.getName()).isEqualTo("홍길동");
		assertThat(result.getEmail()).isEqualTo("gildong@example.com");
	}

	@Test
	@DisplayName("OAuth2Attribute_생년월일_파싱_확인")
	void 카카오_OAuth2Attribute_생년월일_파싱_확인() {
		// given
		Map<String, Object> attributes = new HashMap<>();
		Map<String, Object> kakaoAccount = new HashMap<>();

		attributes.put("id", 123456789L);

        kakaoAccount.put("email", "gildong@example.com");
        kakaoAccount.put("name", "홍길동");
        kakaoAccount.put("birthyear", "2000");
		kakaoAccount.put("birthday", "0127");
		
		attributes.put("kakao_account", kakaoAccount);

		// when
		OAuth2Attribute result = OAuth2Attribute.of(OAuth2Attribute.KAKAO, attributes);

		// then
		assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(2000, 1, 27));
	}
}
