package com.mready.common.config;

import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SecurityConfig {

	private static final String[] WHITELIST = {
		"/swagger-ui/**",
		"/v3/api-docs/**",
		"/error",
		"/"
	};
	private final CorsConfig corsFilter;

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
			.addFilterBefore(corsFilter.corsFilter(), UsernamePasswordAuthenticationFilter.class);

		http
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
				.requestMatchers(WHITELIST).permitAll()
				.anyRequest().permitAll());

		return http.build();
	}
}