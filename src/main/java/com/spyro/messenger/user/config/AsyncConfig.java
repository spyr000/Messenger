package com.spyro.messenger.user.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

@EnableScheduling
@EnableAsync
@ConditionalOnProperty(name="scheduler.enabled", matchIfMissing = true)
@Configuration
public class AsyncConfig implements AsyncConfigurer {
    private final Logger logger = LoggerFactory.getLogger(AsyncUncaughtExceptionHandler.class);
    private static final int MAX_POOL_SIZE = 10;
    private static final int CORE_POOL_SIZE = 2;
    private static final int QUEUE_CAPACITY = 500;
    private static final String THREAD_PREFIX = "TERMINATOR T-";
    @Override
    public Executor getAsyncExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix(THREAD_PREFIX);
        executor.initialize();
        return executor;

    }
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> logger.error(
                new StringBuilder("Unexpected asynchronous exception at : ")
                .append(method.getDeclaringClass().getName())
                .append('.')
                .append(method.getName())
                .toString(),
                ex
        );
    }
}
