package com.mento.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import com.mento.common.config.properties.KakaopayProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableConfigurationProperties(KakaopayProperties.class)
@RequiredArgsConstructor
public class KakaopayConfig {

	private final KakaopayProperties kakaopayProperties;

	@Bean
	public RestClient kakaopayRestClient() {
		return RestClient.builder()
			.baseUrl(kakaopayProperties.baseUrl())
			.defaultHeader("Authorization", "SECRET_KEY " + kakaopayProperties.secretKey())
			.defaultHeader("Content-Type", "application/json")
			.build();
	}
}
