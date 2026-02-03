package com.mento.common.config;

import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.mento.common.auth.handler.OAuth2LoginSuccessHandler;
import com.mento.common.auth.jwt.JwtAuthenticationFilter;
import com.mento.common.auth.jwt.JwtExceptionFilter;
import com.mento.common.auth.service.CustomOAuth2UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SecurityConfig {

	private static final String[] WHITELIST = {
		"/swagger-ui/**",
		"/v3/api-docs/**",
		"/error",
		"/",

		// OAuth2
		"/login/oauth/**",
		"/api/v1/auth/**",
		
		// Test
		"/api/v1/test/**"
	};
	private final CorsConfig corsFilter;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtExceptionFilter jwtExceptionFilter;

	private static void createSessionPolicy(SessionManagementConfigurer<HttpSecurity> session) {
		session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.sessionManagement(SecurityConfig::createSessionPolicy)
			.addFilterBefore(corsFilter.corsFilter(), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);

		http
			.oauth2Login(oauth2 -> oauth2
				.userInfoEndpoint(userInfo -> userInfo
					.userService(customOAuth2UserService))
				.successHandler(oAuth2LoginSuccessHandler))
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
				.requestMatchers(WHITELIST).permitAll()
				.anyRequest().authenticated());

		return http.build();
	}
}