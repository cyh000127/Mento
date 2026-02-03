package com.mento.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

public class AiServerConfig {
	@Value("")
	private String aiServerUrl;

	@Bean
	public RestClient skinAnalyzerClient() {
		return RestClient.builder()
			.baseUrl(aiServerUrl)
			.build();
	}
}
