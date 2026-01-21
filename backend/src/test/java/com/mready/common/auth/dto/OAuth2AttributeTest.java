package com.mready.common.auth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2AttributeTest {

	@Test
	@DisplayName("카카오_OAuth2Attribute_변환_확인")
	void 카카오_OAuth2Attribute_변환_확인() {
		// given
		Map<String, Object> attributes = new HashMap<>();
		Map<String, Object> kakaoAccount = new HashMap<>();
		Map<String, Object> profile = new HashMap<>();

		attributes.put("id", 123456789L); // Long type id
		
		profile.put("nickname", "testUser");
		profile.put("profile_image_url", "http://example.com/image.jpg");
		
		kakaoAccount.put("profile", profile);
		kakaoAccount.put("email", "test@example.com");
		kakaoAccount.put("name", "Test Name");
		
		attributes.put("kakao_account", kakaoAccount);

		// when
		OAuth2Attribute result = OAuth2Attribute.of("KAKAO", attributes);

		// then
		assertThat(result.getProvider()).isEqualTo("KAKAO");
		assertThat(result.getProviderId()).isEqualTo("KAKAO_123456789"); // Check prefix
		assertThat(result.getName()).isEqualTo("Test Name");
		assertThat(result.getEmail()).isEqualTo("test@example.com");
	}
}
