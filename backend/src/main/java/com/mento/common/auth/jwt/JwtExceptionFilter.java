package com.mento.common.auth.jwt;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.AuthException;
import com.mento.common.response.ErrorResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (AuthException e) {
			log.info("인증 예외 발생: {} | 예외 발생 지점 [{} {}]", e.getMessage(), request.getMethod(), request.getRequestURI());
			handleException(request, response, e.getErrorCode());
		} catch (Exception e) {
			log.info("보안 필터 체인 미처리 예외 발생 | 예외 발생 지점 [{} {}]", request.getMethod(), request.getRequestURI(), e);
			handleException(request, response, ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	private void handleException(
		HttpServletRequest request,
		HttpServletResponse response,
		ErrorCode errorCode
	) throws IOException {
		response.setStatus(errorCode.getHttpStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");

		ErrorResponse errorResponse = ErrorResponse.of(errorCode, request);
		String json = objectMapper.writeValueAsString(errorResponse);

		response.getWriter().write(json);
	}
}
