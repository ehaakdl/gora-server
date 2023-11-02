package org.gora.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableAsync
public class ScheduleConfig implements SchedulingConfigurer {

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // Thread Pool 설정
		ThreadPoolTaskScheduler threadPool = new ThreadPoolTaskScheduler();  
 
        // Thread 개수 설정
		int n = Runtime.getRuntime().availableProcessors();
		threadPool.setPoolSize(n);
		threadPool.initialize();
        
		taskRegistrar.setTaskScheduler(threadPool);
	}
}
