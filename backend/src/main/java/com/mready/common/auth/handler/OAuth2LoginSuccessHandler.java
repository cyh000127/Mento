package com.mready.common.auth.handler;

import com.mready.common.auth.constant.AuthConstant;
import com.mready.common.auth.dto.Token;
import com.mready.common.auth.entity.RefreshToken;
import com.mready.common.auth.jwt.JwtProperties;
import com.mready.common.auth.jwt.JwtTokenProvider;
import com.mready.common.auth.principal.CustomOAuth2User;
import com.mready.common.auth.repository.RefreshTokenRepository;
import com.mready.common.constant.FrontDomain;
import com.mready.common.error.ErrorCode;
import com.mready.common.error.exception.BusinessException;
import com.mready.common.util.CookieUtil;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtTokenProvider jwtTokenProvider;
	private final JwtProperties jwtProperties;
	private final RefreshTokenRepository refreshTokenRepository;

	@Override
	public void onAuthenticationSuccess(
			@Nonnull HttpServletRequest request,
			@Nonnull HttpServletResponse response,
			@Nonnull Authentication authentication
	) throws IOException {
		CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();

		if (user == null) {
			throw new BusinessException(ErrorCode.AUTHENTICATION_PRINCIPAL_NOT_FOUND);
		}
		if (user.getMember() == null) {
			throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
		}

		Token token = jwtTokenProvider.createToken(user.getMember());

		// Refresh Token 쿠키 설정 (CookieUtil 사용)
		CookieUtil.addCookie(response, "refreshToken", token.refreshToken(),
				(int) (jwtProperties.refreshTokenExpiration() / 1000));

		// 2. Redis에 Refresh Token 저장
		RefreshToken refreshToken = RefreshToken.builder()
				.memberId(String.valueOf(user.getMember().getId()))
				.token(token.refreshToken())
				.expirationTime(jwtProperties.refreshTokenExpiration() / 1000)
				.build();

		refreshTokenRepository.save(refreshToken);

		// Access Token 헤더 설정
		response.setHeader(AuthConstant.AUTHORIZATION, AuthConstant.BEARER + token.accessToken());

		// 리다이렉트 (Access Token을 쿼리 파라미터로 포함)
		String targetUrl = UriComponentsBuilder.fromUriString(FrontDomain.LOCAL.getUrl() + "/login/oauth2/callback")
				.queryParam("accessToken", token.accessToken())
				.build().toUriString();

		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}
