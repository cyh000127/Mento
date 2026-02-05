package com.mento.common.livekit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import io.livekit.server.RoomServiceClient;

@SpringJUnitConfig
@EnableConfigurationProperties(LiveKitProperties.class)
@ContextConfiguration(classes = {LiveKitManager.class, LiveKitProperties.class,
	LiveKitConfigurationTest.MockConfig.class}, initializers = ConfigDataApplicationContextInitializer.class)
@TestPropertySource(properties = {
	"livekit.host=wss://dummy.url",
	"livekit.api-key=dummyKey",
	"livekit.secret=dummySecret"
})
class LiveKitConfigurationTest {

	@TestConfiguration
	static class MockConfig {
		@Bean
		RoomServiceClient roomServiceClient() {
			return mock(RoomServiceClient.class);
		}
	}

	@Autowired
	private LiveKitProperties liveKitProperties;

	@Autowired
	private LiveKitManager liveKitManager;


	@Test
	@DisplayName("LiveKit 설정 로드 및 빈 주입 검증")
	void verifyLiveKitConfiguration() {
		assertThat(liveKitProperties).isNotNull();
		assertThat(liveKitProperties.getHost()).isEqualTo("wss://dummy.url");

		assertThat(liveKitManager).isNotNull();
		assertThat(liveKitManager.getUrl()).isEqualTo(liveKitProperties.getHost());
	}
}
