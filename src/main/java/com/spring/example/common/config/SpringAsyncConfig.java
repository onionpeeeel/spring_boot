package com.spring.example.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@EnableAsync
@Configuration
public class SpringAsyncConfig {

    /**
     * Async Task 설정
     * Async Thread 시큐리티 정보 획득
     * Async Thread 세션 사용
     */
    @Bean(name = "threadPoolTaskExecutor")
    public TaskExecutor threadPoolTaskExecutor() {
        int CORE_POOL_SIZE = 1;
        int MAX_POOL_SIZE = 10;
        int QUEUE_CAPACITY = 100;

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        taskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        taskExecutor.setQueueCapacity(QUEUE_CAPACITY);
        taskExecutor.setTaskDecorator(new CustomDecorator());
        taskExecutor.setThreadNamePrefix("Executor-");
        taskExecutor.initialize();

        return new DelegatingSecurityContextAsyncTaskExecutor(taskExecutor);
    }

    /**
     * Async 에서 세션 사용
     */
    public static class CustomDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            return () -> {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                runnable.run();
            };
        }
    }
}
