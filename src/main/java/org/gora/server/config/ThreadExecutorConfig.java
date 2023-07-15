package org.gora.server.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.gora.server.common.CommonUtils;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class ThreadExecutorConfig implements AsyncConfigurer {
    @Bean(name = "executor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //기본 Thread 수
        executor.setCorePoolSize(10);
        //최대 Thread 수
        executor.setMaxPoolSize(10);
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
