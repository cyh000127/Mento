package com.mento.common.config.restclient;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import com.mento.common.config.properties.KakaopayProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableConfigurationProperties(KakaopayProperties.class)
@RequiredArgsConstructor
public class RestClientConfig {

	private final KakaopayProperties kakaopayProperties;

	@Bean(name = "skinAnalysisRestClient")
	public RestClient skinAnalysisRestClient() {
		return RestClient.builder()
			.baseUrl("http://localhost:8000")
			.build();
	}

	@Bean(name = "kakaopayRestClient")
	public RestClient kakaopayRestClient() {
		return RestClient.builder()
			.baseUrl(kakaopayProperties.baseUrl())
			.defaultHeader("Authorization", "SECRET_KEY " + kakaopayProperties.secretKey())
			.defaultHeader("Content-Type", "application/json")
			.build();
	}

}
