package com.mento.common.auth.handler;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.net.URI;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;

import com.mento.common.auth.constant.AuthConstant;
import com.mento.common.auth.dto.Token;
import com.mento.common.auth.jwt.JwtProperties;
import com.mento.common.auth.jwt.JwtTokenProvider;
import com.mento.common.auth.principal.CustomOAuth2User;
import com.mento.common.constant.FrontDomain;
import com.mento.domain.user.entity.User;

import jakarta.servlet.http.Cookie;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth2LoginSuccessHandler 단위 테스트")
class OAuth2LoginSuccessHandlerTest {

	@InjectMocks
	OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

	@Mock
	JwtTokenProvider jwtTokenProvider;

	@Mock
	JwtProperties jwtProperties;

	@Mock
	Authentication authentication;

	@Mock
	CustomOAuth2User customOAuth2User;

	@Mock
	User user;

	@DisplayName("refreshToken_accessToken_발급_확인")
	@Test
	void refreshToken_accessToken_발급_확인() throws Exception {
		// given
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		given(authentication.getPrincipal()).willReturn(customOAuth2User);
		given(customOAuth2User.getUser()).willReturn(user);

		Token token = new Token("access-token", "refresh-token");
		given(jwtTokenProvider.createToken(any(User.class))).willReturn(token);

		given(jwtProperties.refreshTokenExpiration()).willReturn(1209600000L); // 14일 (밀리초)

		// when
		oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

		// then
		// Refresh Token 쿠키 검증 (CookieUtil)
		Cookie refreshTokenCookie = response.getCookie("refreshToken");
		assertThat(refreshTokenCookie).isNotNull();
		assertThat(refreshTokenCookie.getValue()).isEqualTo("refresh-token");
		assertThat(refreshTokenCookie.isHttpOnly()).isTrue();
		assertThat(refreshTokenCookie.getPath()).isEqualTo("/");
		assertThat(refreshTokenCookie.getMaxAge()).isEqualTo(1209600); // 1209600000 / 1000

		// Access Token 헤더 검증
		String authHeader = response.getHeader(AuthConstant.AUTHORIZATION);
		assertThat(authHeader)
			.isNotNull()
			.isEqualTo(AuthConstant.BEARER + "access-token");

		// 리다이렉트 URL 검증
		String redirectedUrl = response.getRedirectedUrl();
		assertThat(redirectedUrl).isNotNull();

		URI uri = URI.create(redirectedUrl);
		assertThat(uri.getQuery()).contains("accessToken=access-token");
		assertThat(redirectedUrl).contains(FrontDomain.LOCAL.getUrl() + "/login/oauth2/callback");
	}
}
