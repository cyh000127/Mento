package com.mento.common.auth.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.mento.common.auth.constant.AuthConstant;
import com.mento.common.auth.dto.Token;
import com.mento.common.auth.jwt.JwtProperties;
import com.mento.common.auth.jwt.JwtTokenProvider;
import com.mento.common.auth.principal.CustomOAuth2User;
import com.mento.common.constant.FrontDomain;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;
import com.mento.common.util.CookieUtil;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtTokenProvider jwtTokenProvider;
	private final JwtProperties jwtProperties;

	@Override
	public void onAuthenticationSuccess(
		@Nonnull HttpServletRequest request,
		@Nonnull HttpServletResponse response,
		@Nonnull Authentication authentication
	) throws IOException {
		CustomOAuth2User user = (CustomOAuth2User)authentication.getPrincipal();

		if (user == null) {
			throw new BusinessException(ErrorCode.AUTHENTICATION_PRINCIPAL_NOT_FOUND);
		}
		if (user.getUser() == null) {
			throw new BusinessException(ErrorCode.USER_NOT_FOUND);
		}

		Token token = jwtTokenProvider.createToken(user.getUser());

		CookieUtil.addCookie(response, "refreshToken", token.refreshToken(),
			(int)(jwtProperties.refreshTokenExpiration() / 1000));

		response.setHeader(AuthConstant.AUTHORIZATION, AuthConstant.BEARER + token.accessToken());

		String targetUrl = UriComponentsBuilder.fromUriString(FrontDomain.LOCAL.getUrl() + "/login/oauth2/callback")
			.queryParam("accessToken", token.accessToken())
			.build().toUriString();

		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}
