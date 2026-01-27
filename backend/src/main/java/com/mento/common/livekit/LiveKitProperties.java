package com.mento.common.livekit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "livekit")
public class LiveKitProperties {
	private String url;
	private String apiKey;
	private String secret;
}
