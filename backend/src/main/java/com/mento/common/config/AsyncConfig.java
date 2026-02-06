package com.mento.common.config;

import java.util.concurrent.Executor;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.mento.common.config.properties.AsyncProperties;

@Configuration
@EnableAsync
@EnableConfigurationProperties(AsyncProperties.class)
public class AsyncConfig {

	private static final int AI_RETRY_MAX_ATTEMPTS = 3;

	@Bean("aiUploadThreadPoolExecutor")
	public Executor aiUploadThreadPoolExecutor(AsyncProperties asyncProperties) {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(asyncProperties.corePoolSize());
		taskExecutor.setMaxPoolSize(asyncProperties.maxPoolSize());
		taskExecutor.setQueueCapacity(asyncProperties.queueCapacity());
		taskExecutor.setThreadNamePrefix("mento-ai");
		taskExecutor.initialize();
		return taskExecutor;
	}

	@Bean
	public ClientHttpRequestInterceptor clientHttpRequestInterceptor() {
		RetryTemplate retryTemplate = new RetryTemplate();
		retryTemplate.setRetryPolicy(new SimpleRetryPolicy(AI_RETRY_MAX_ATTEMPTS));

		return (request, body, execution) ->
			retryTemplate.execute(_ -> execution.execute(request, body));
	}
}