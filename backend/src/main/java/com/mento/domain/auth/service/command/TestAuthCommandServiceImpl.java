package com.mento.domain.auth.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.auth.dto.Token;
import com.mento.common.auth.jwt.JwtTokenProvider;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.AuthException;
import com.mento.common.util.CookieUtil;
import com.mento.domain.auth.dto.request.TestLoginReqDto;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.service.query.UserQueryServiceImpl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TestAuthCommandServiceImpl implements TestAuthCommandService {

	private final JwtTokenProvider jwtTokenProvider;
	private final UserQueryServiceImpl userQueryService;

	@Override
	public Token login(TestLoginReqDto reqDto, HttpServletResponse response) {
		User user = userQueryService.findByEmail(reqDto.email());
		if (!user.getPassword().equals(reqDto.password())) {
			throw new AuthException(ErrorCode.INVALID_PASSWORD);
		}
		// 24시간 유효 토큰 생성
		Token token = jwtTokenProvider.createTestToken(user);
		
		// Refresh Token을 쿠키에 설정 (24시간)
		CookieUtil.addCookie(response, "refreshToken", token.refreshToken(), 24 * 60 * 60);
		
		return token;
	}
}
