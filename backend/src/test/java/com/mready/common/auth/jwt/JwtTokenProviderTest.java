package com.mready.common.auth.jwt;

import com.mready.common.auth.dto.Token;
import com.mready.common.error.exception.BusinessException;
import com.mready.domain.member.entity.Member;
import com.mready.domain.member.repository.MemberRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.lenient;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

	@Mock
	private JwtProperties jwtProperties;

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private JwtTokenProvider jwtTokenProvider;

	private final String secretKey = "testSecretKeytestSecretKeytestSecretKeytestSecretKey"; // 32byte 이상
	private final Long accessTokenExpiration = 3600000L;
	private final Long refreshTokenExpiration = 7200000L;

	@BeforeEach
	void setUp() {
		lenient().when(jwtProperties.secret()).thenReturn(secretKey);
		lenient().when(jwtProperties.accessTokenExpiration()).thenReturn(accessTokenExpiration);
		lenient().when(jwtProperties.refreshTokenExpiration()).thenReturn(refreshTokenExpiration);
	}

	@Test
	@DisplayName("토큰_생성")
	void 토큰_생성() {
		// given
		Member member = Member.builder()
			.providerId("123456789") // providerId
			.build();

		// when
		Token token = jwtTokenProvider.createToken(member);

		// then
		assertThat(token).isNotNull();
		assertThat(token.accessToken()).isNotEmpty();
		assertThat(token.refreshToken()).isNotEmpty();
	}

	@Test
	@DisplayName("유효한_토큰_검증")
	void 유효한_토큰_검증() {
		// given
		Member member = Member.builder().providerId("123456789").build();
		String accessToken = jwtTokenProvider.createToken(member).accessToken();

		// when & then
		// 예외가 발생하지 않음을 검증
		jwtTokenProvider.validateToken(accessToken);
	}

	@Test
	@DisplayName("만료된_토큰_검증")
	void 만료된_토큰_검증() {
		// given
		// 만료 시간을 짧게 설정하여 토큰 생성
		String expiredToken = createExpiredToken("123456789");

		// when & then
		assertThatThrownBy(() -> jwtTokenProvider.validateToken(expiredToken))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("토큰에서_Member_정보_조회")
	void 토큰에서_Member_정보_조회() {
		// given
		String socialId = "123456789";
		Member member = Member.builder().providerId(socialId).build();
		String accessToken = jwtTokenProvider.createToken(member).accessToken();

		given(memberRepository.findByProviderId(socialId)).willReturn(Optional.of(member));

		// when
		Optional<Member> result = jwtTokenProvider.getMember(accessToken);

		// then
		assertThat(result).isPresent();
		assertThat(result.get().getProviderId()).isEqualTo(socialId);
	}

	private String createExpiredToken(String socialId) {
		Date now = new Date();
		Date past = new Date(now.getTime() - 1000); // 1초 전 만료

		return Jwts.builder()
			.subject(socialId)
			.claim("type", "ACCESS_TOKEN")
			.issuedAt(past)
			.expiration(past)
			.signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
			.compact();
	}
}
