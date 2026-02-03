package com.mento.common.config;

import java.util.Objects;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import com.mento.common.advisor.CustomLoggingAdvisor;
import com.mento.common.config.properties.OpenAiProperties;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableConfigurationProperties({
	OpenAiProperties.class,
})
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AiConfig {

	private final CustomLoggingAdvisor customLoggingAdvisor;
	private final OpenAiProperties openAiProperties;

	@Bean
	public ChatClient chatClient(final ChatModel chatModel) {
		return ChatClient.builder(chatModel)
			.defaultAdvisors(customLoggingAdvisor)
			.build();
	}

	@Bean
	public ChatModel chatModel(final OpenAiApi openAiApi) {
		OpenAiChatOptions chatOptions = createOpenAiChatOptions();

		return OpenAiChatModel.builder()
			.openAiApi(openAiApi)
			.defaultOptions(chatOptions)
			.build();
	}

	@Bean
	public OpenAiApi openAiApi(final RestClient.Builder restClientBuilder) {
		String baseUrl = openAiProperties.baseUrl();
		String apiKey = Objects.requireNonNull(openAiProperties.apiKey(), "OpenAI API Key는 필수입니다");

		String model = openAiProperties.chat().options().model();
		log.info("[AiConfig] OpenAI API 초기화 {baseUrl: {}, model: {}}", baseUrl, model);

		return OpenAiApi.builder()
			.baseUrl(baseUrl)
			.apiKey(apiKey)
			.restClientBuilder(restClientBuilder)
			.build();
	}

	@Bean
	public RestClient.Builder restClientBuilder() {
		return RestClient.builder()
			.requestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
	}

	private OpenAiChatOptions createOpenAiChatOptions() {
		String model = openAiProperties.chat().options().model();
		return OpenAiChatOptions.builder()
			.model(model)
			// .maxTokens(10000)
			.build();
	}
}