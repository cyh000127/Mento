package com.mento.common.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import com.mento.common.advisor.CustomLoggingAdvisor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AiConfig {

	private final CustomLoggingAdvisor customLoggingAdvisor;

	@Bean
	public ChatClient chatClient(final ChatModel chatModel) {
		return ChatClient.builder(chatModel)
			.defaultAdvisors(customLoggingAdvisor)
			.build();
	}

	@Bean
	public RestClient.Builder restClientBuilder() {
		return RestClient.builder()
			.requestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
	}
}