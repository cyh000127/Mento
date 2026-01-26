package com.mready.common.config;

import com.mready.common.auth.handler.OAuth2LoginSuccessHandler;
import com.mready.common.auth.jwt.JwtAuthenticationFilter;
import com.mready.common.auth.jwt.JwtExceptionFilter;
import com.mready.common.auth.service.CustomOAuth2UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
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
			"/login/oauth/**"
	};
	private final CorsConfig corsFilter;

	private static void createSessionPolicy(SessionManagementConfigurer<HttpSecurity> session) {
		session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtExceptionFilter jwtExceptionFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) {
		try {
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
							.anyRequest().permitAll());

			return http.build();
		} catch (Exception e) {
			throw new RuntimeException("Security Filter Chain 구성 중 오류가 발생했습니다.", e);
		}
	}
}