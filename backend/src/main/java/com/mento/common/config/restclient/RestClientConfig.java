package com.mento.common.config.restclient;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import com.mento.common.config.properties.KakaopayProperties;
import com.mento.common.config.properties.SkinAnalysisProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableConfigurationProperties({
	KakaopayProperties.class,
	SkinAnalysisProperties.class
})
@RequiredArgsConstructor
public class RestClientConfig {

	private final KakaopayProperties kakaopayProperties;
	private final SkinAnalysisProperties skinAnalysisProperties;

	@Bean(name = "skinAnalysisRestClient")
	public RestClient skinAnalysisRestClient() {
		return RestClient.builder()
			.baseUrl(skinAnalysisProperties.baseUrl())
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
