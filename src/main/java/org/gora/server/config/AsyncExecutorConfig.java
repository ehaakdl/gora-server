package org.gora.server.config;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import org.gora.server.common.CommonUtils;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableAsync
public class AsyncExecutorConfig implements AsyncConfigurer {
    @Value("${app.async.corePoolSize}")
    private int corePoolSize;
    @Value("${app.async.maxPoolSize}")
    private int maxPoolSize;
    @Bean(name = "executor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //기본 Thread 수
        executor.setCorePoolSize(corePoolSize);
        //최대 Thread 수
        executor.setMaxPoolSize(maxPoolSize);
        //QUEUE 수
        executor.setQueueCapacity(100);

        //Thread Bean Name
        String EXECUTOR_BEAN_NAME = "executor";
        executor.setBeanName(EXECUTOR_BEAN_NAME);
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }

    @Slf4j
    static class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
        @Override
        public void handleUncaughtException(Throwable ex, Method method, Object... params) {
            log.error("스레드 처리 중 에러 발생");
            log.error(CommonUtils.getStackTraceElements(ex));
        }
    }
}
