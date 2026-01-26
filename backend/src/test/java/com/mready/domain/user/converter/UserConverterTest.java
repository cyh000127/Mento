package com.mready.domain.user.converter;

import com.mready.common.auth.dto.OAuth2Attribute;
import com.mready.domain.user.dto.response.UserResDto;
import com.mready.domain.user.entity.Role;
import com.mready.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserConverterTest")
class UserConverterTest {

	@Test
	@DisplayName("toEntity_OAuth2Attribute_변환_확인_및_비밀번호_해시_검증")
	void toEntity_OAuth2Attribute_변환_확인() {
		// given
		Map<String, Object> attributes = new HashMap<>();
		String kakaoId = "kakao_12345";
		String email = "test@example.com";
		
		attributes.put("id", 12345);
		
		OAuth2Attribute oAuth2Attribute = OAuth2Attribute.builder()
				.kakaoId(kakaoId)
				.name("Test User")
				.email(email)
				.birthDate(LocalDate.of(2000, 1, 1))
				.attributes(attributes)
				.build();

		// when
		User user = UserConverter.toEntity(oAuth2Attribute);

		// then
		assertThat(user.getKakaoId()).isEqualTo(kakaoId);
		assertThat(user.getEmail()).isEqualTo(email);
		assertThat(user.getBirthDate()).isEqualTo(LocalDate.of(2000, 1, 1));
		
		// Password Hash Logic Verification
		String expectedPassword = String.valueOf(kakaoId.hashCode());
		assertThat(user.getPassword()).isEqualTo(expectedPassword);
	}

	@Test
	@DisplayName("toUserResDto_변환_확인_birthDate_포함")
	void toUserResDto_변환_확인() {
		// given
		User user = User.builder()
				.id(1L)
				.name("Test User")
				.email("test@example.com")
				.password("password")
				.kakaoId("kakao_12345")
				.birthDate(LocalDate.of(1995, 5, 5))
				.role(Role.USER)
				.build();

		// when
		UserResDto resDto = UserConverter.toUserResDto(user);

		// then
		assertThat(resDto.id()).isEqualTo(1L);
		assertThat(resDto.birthDate()).isEqualTo(LocalDate.of(1995, 5, 5));
	}
}
