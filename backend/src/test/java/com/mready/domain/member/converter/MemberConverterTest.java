package com.mready.domain.member.converter;

import com.mready.common.auth.dto.OAuth2Attribute;
import com.mready.domain.member.dto.response.MemberResDto;
import com.mready.domain.member.entity.Member;
import com.mready.domain.member.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MemberConverterTest")
class MemberConverterTest {

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
		Member member = MemberConverter.toEntity(oAuth2Attribute);

		// then
		assertThat(member.getKakaoId()).isEqualTo(kakaoId);
		assertThat(member.getEmail()).isEqualTo(email);
		assertThat(member.getBirthDate()).isEqualTo(LocalDate.of(2000, 1, 1));
		
		// Password Hash Logic Verification
		String expectedPassword = String.valueOf(kakaoId.hashCode());
		assertThat(member.getPassword()).isEqualTo(expectedPassword);
	}

	@Test
	@DisplayName("toMemberResDto_변환_확인_birthDate_포함")
	void toMemberResDto_변환_확인() {
		// given
		Member member = Member.builder()
				.id(1L)
				.name("Test User")
				.email("test@example.com")
				.password("password")
				.kakaoId("kakao_12345")
				.birthDate(LocalDate.of(1995, 5, 5))
				.role(Role.USER)
				.build();

		// when
		MemberResDto resDto = MemberConverter.toMemberResDto(member);

		// then
		assertThat(resDto.id()).isEqualTo(1L);
		assertThat(resDto.birthDate()).isEqualTo(LocalDate.of(1995, 5, 5));
	}
}
