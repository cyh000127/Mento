package com.mento.common.config.restclient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchOperationsContainer {

	private final ElasticsearchOperations elasticsearchOperations;

	public ElasticsearchOperationsContainer(
		@Qualifier("elasticsearchOperations") ElasticsearchOperations elasticsearchOperations) {
		this.elasticsearchOperations = elasticsearchOperations;
	}

	public ElasticsearchOperations get() {
		return elasticsearchOperations;
	}
}
