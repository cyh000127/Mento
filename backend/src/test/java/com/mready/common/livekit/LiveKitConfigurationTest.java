package com.mready.common.livekit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
class LiveKitConfigurationTest {

    @Autowired
    private LiveKitProperties liveKitProperties;

    @Autowired
    private LiveKitManager liveKitManager;

    @Test
    @DisplayName("LiveKit 설정 로드 및 빈 주입 검증")
    void verifyLiveKitConfiguration() {
        System.out.println("LiveKit URL: " + liveKitProperties.getUrl());

        assertThat(liveKitProperties).isNotNull();
        
        assertThat(liveKitManager).isNotNull();
        assertThat(liveKitManager.getUrl()).isEqualTo(liveKitProperties.getUrl());
    }
}
