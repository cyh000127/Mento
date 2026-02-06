package com.mento.common.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

	private static final int AI_RETRY_MAX_ATTEMPTS = 3;

	@Bean("aiUploadThreadPoolExecutor")
	public Executor aiUploadThreadPoolExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(5);
		taskExecutor.setMaxPoolSize(20);
		taskExecutor.setQueueCapacity(1000);
		taskExecutor.setThreadNamePrefix("mento-ai");
		taskExecutor.initialize();
		return taskExecutor;
	}

	public ClientHttpRequestInterceptor clientHttpRequestInterceptor() {
		RetryTemplate retryTemplate = new RetryTemplate();
		retryTemplate.setRetryPolicy(new SimpleRetryPolicy(AI_RETRY_MAX_ATTEMPTS));

		return (request, body, execution) ->
			retryTemplate.execute(_ -> execution.execute(request, body));
	}
}