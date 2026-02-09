package com.mento.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.mento.common.error.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableScheduling
public class SchedulerConfig implements SchedulingConfigurer {

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		ThreadPoolTaskScheduler threadPool = new ThreadPoolTaskScheduler();
		
		int poolSize = Runtime.getRuntime().availableProcessors() * 2;
		threadPool.setPoolSize(poolSize);
		threadPool.setThreadNamePrefix("scheduler-thread-");
		threadPool.setErrorHandler(t -> 
			log.error("[Scheduler] 스케줄러 오류 :{} : ", ErrorCode.SCHEDULER_ERROR.getMessage(), t));
		threadPool.initialize();
		
		taskRegistrar.setTaskScheduler(threadPool);
	}
}
