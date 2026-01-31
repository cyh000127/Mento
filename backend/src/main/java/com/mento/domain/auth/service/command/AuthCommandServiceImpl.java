package com.mento.domain.auth.service.command;

import static com.mento.common.auth.constant.AuthConstant.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.auth.constant.AuthConstant;
import com.mento.common.auth.dto.Token;
import com.mento.common.auth.jwt.JwtProperties;
import com.mento.common.auth.jwt.JwtTokenProvider;
import com.mento.common.auth.redis.repository.BlackListRepository;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.AuthException;
import com.mento.common.error.exception.BusinessException;
import com.mento.common.util.CookieUtil;
import com.mento.domain.user.entity.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthCommandServiceImpl implements AuthCommandService {

	private final JwtTokenProvider jwtTokenProvider;
	private final BlackListRepository blackListRepository;
	private final JwtProperties jwtProperties;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = CookieUtil.getCookie(request, AuthConstant.REFRESH_TOKEN)
			.orElse(null);
		String accessToken = jwtTokenProvider.extractAccessToken(request)
			.orElseThrow(() -> new AuthException(ErrorCode.TOKEN_NOT_FOUND));

		if (refreshToken != null) {
			jwtTokenProvider.setBlackList(refreshToken);
			CookieUtil.deleteCookie(request, response, AuthConstant.REFRESH_TOKEN);
		}

		jwtTokenProvider.setBlackList(accessToken);
	}

	@Override
	public void reissue(HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = CookieUtil.getCookie(request, AuthConstant.REFRESH_TOKEN)
			.orElseThrow(() -> new AuthException(ErrorCode.TOKEN_NOT_FOUND));

		jwtTokenProvider.validateRefreshToken(refreshToken);

		if (blackListRepository.existsById(refreshToken)) {
			throw new AuthException(ErrorCode.TOKEN_BLACKLISTED_EXCEPTION);
		}

		User user = jwtTokenProvider.getUser(refreshToken)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		if (user.getDeletedAt() != null) {
			throw new AuthException(ErrorCode.ALREADY_WITHDRAWN);
		}

		Token newToken = jwtTokenProvider.createToken(user);

		response.setHeader(AUTHORIZATION, BEARER + newToken.accessToken());
		CookieUtil.addCookie(response, AuthConstant.REFRESH_TOKEN, newToken.refreshToken(),
			(int)(jwtProperties.refreshTokenExpiration() / 1000));

		jwtTokenProvider.setBlackList(refreshToken);
	}
}
