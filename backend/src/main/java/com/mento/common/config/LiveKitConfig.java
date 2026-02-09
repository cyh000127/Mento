package com.mento.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mento.common.livekit.LiveKitProperties;

import io.livekit.server.EgressServiceClient;
import io.livekit.server.RoomServiceClient;
import io.livekit.server.WebhookReceiver;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(LiveKitProperties.class)
public class LiveKitConfig {

	private final LiveKitProperties liveKitProperties;

	@Bean
	public RoomServiceClient roomServiceClient() {
		return RoomServiceClient.create(
			liveKitProperties.getHost(),
			liveKitProperties.getApiKey(),
			liveKitProperties.getSecret()
		);
	}

	@Bean
	public EgressServiceClient egressServiceClient() {
		return EgressServiceClient.create(
			liveKitProperties.getHost(),
			liveKitProperties.getApiKey(),
			liveKitProperties.getSecret()
		);
	}

	@Bean
	public WebhookReceiver webhookReceiver() {
		return new WebhookReceiver(
			liveKitProperties.getApiKey(),
			liveKitProperties.getSecret()
		);
	}
}
