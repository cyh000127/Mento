package com.mento.common.auth.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mento.common.auth.principal.AuthenticatedUser;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return path.startsWith("/api/v1/auth/reissue");
	}

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		try {
			jwtTokenProvider.extractAccessToken(request)
				.filter(token -> {
					jwtTokenProvider.validateToken(token);
					return jwtTokenProvider.getUser(token).isPresent();
				})
				.ifPresent(this::setAuthentication);
		} catch (Exception e) {
			SecurityContextHolder.clearContext();
		}

		filterChain.doFilter(request, response);
	}

	private void setAuthentication(String accessToken) {
		AuthenticatedUser authuser = jwtTokenProvider.getAuthenticatedUser(accessToken);
		Authentication authentication = new UsernamePasswordAuthenticationToken(authuser, null,
			authuser.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
