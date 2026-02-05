package com.mento.common.config.properties;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.elasticsearch")
public record ElasticsearchCustomProperties(
	List<String> uris,
	Duration connectionTimeout,
	Duration socketTimeout
) {
}
