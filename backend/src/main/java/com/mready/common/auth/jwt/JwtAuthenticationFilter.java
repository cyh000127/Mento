package com.mready.common.auth.jwt;

import com.mready.common.auth.principal.CustomOAuth2User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		jwtTokenProvider.extractAccessToken(request)
			.filter(token -> {
				jwtTokenProvider.validateToken(token);
				return true;
			})
			.ifPresent(this::setAuthentication);

		filterChain.doFilter(request, response);
	}

	private void setAuthentication(String accessToken) {
		// 토큰에서 사용자 정보 추출하여 SecurityContext에 설정
		jwtTokenProvider.getMember(accessToken).ifPresent(member -> {
			CustomOAuth2User customOAuth2User = new CustomOAuth2User(member);
			Authentication authentication = new UsernamePasswordAuthenticationToken(
				customOAuth2User,
				null,
				customOAuth2User.getAuthorities()
			);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		});
	}
}
