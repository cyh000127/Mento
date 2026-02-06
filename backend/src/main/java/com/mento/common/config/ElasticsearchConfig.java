package com.mento.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.support.HttpHeaders;

import com.mento.common.config.properties.ElasticsearchCustomProperties;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableConfigurationProperties(ElasticsearchCustomProperties.class)
@RequiredArgsConstructor
public class ElasticsearchConfig extends ElasticsearchConfiguration {

	private final ElasticsearchCustomProperties properties;

	@Override
	public ClientConfiguration clientConfiguration() {
		return ClientConfiguration.builder()
			.connectedTo(properties.uris().toArray(new String[0]))
			.withConnectTimeout(properties.connectionTimeout())
			.withSocketTimeout(properties.socketTimeout())
			.withHeaders(() -> {
				HttpHeaders headers = new HttpHeaders();
				headers.set("Content-Type", "application/json");
				headers.set("Accept", "application/json");
				return headers;
			})
			.build();
	}

	@Bean(name = {"elasticsearchOperations", "elasticsearchTemplate"})
	@Override
	public ElasticsearchOperations elasticsearchOperations(ElasticsearchConverter elasticsearchConverter,
		ElasticsearchClient elasticsearchClient) {
		return super.elasticsearchOperations(elasticsearchConverter, elasticsearchClient);
	}
}
