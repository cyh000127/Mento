package com.mento.common.config;

import java.util.Map;
import java.util.concurrent.Executor;

import org.slf4j.MDC;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.retry.RetryException;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.mento.common.config.properties.AsyncProperties;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.AiException;

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
		taskExecutor.setTaskDecorator(new MdcPropagatingTaskDecorator());
		taskExecutor.initialize();
		return taskExecutor;
	}

	@Bean("aiRetryInterceptor")
	public ClientHttpRequestInterceptor aiRetryInterceptor() {
		RetryTemplate retryTemplate = new RetryTemplate();
		retryTemplate.setRetryPolicy(new SimpleRetryPolicy(AI_RETRY_MAX_ATTEMPTS));

		return (request, body, execution) -> {
			try {
				return retryTemplate.execute(_ -> execution.execute(request, body));
			} catch (RetryException _) {
				throw new AiException(ErrorCode.AI_REQUEST_RETRY_FAILED);
			}
		};
	}

	private static class MdcPropagatingTaskDecorator implements TaskDecorator {
		@Override
		public Runnable decorate(Runnable runnable) {
			Map<String, String> contextMap = MDC.getCopyOfContextMap();

			return () -> {
				try {
					if (contextMap != null) {
						MDC.setContextMap(contextMap);
					}
					runnable.run();
				} finally {
					MDC.clear();
				}
			};
		}
	}
}