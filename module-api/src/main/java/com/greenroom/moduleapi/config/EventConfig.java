package com.greenroom.moduleapi.config;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.greenroom.moduleapi.eventlistener.SearchEventListener;
import com.greenroom.moduleapi.service.question.search.QuestionSearchService;
import com.greenroom.modulecommon.event.EventExceptionHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Getter
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "eventbus.thread")
public class EventConfig {

    private final String threadNamePrefix;
    private final int corePoolSize;
    private final int maxPoolSize;
    private final int queueCapacity;

    @Bean(name = "eventTaskExecutor")
    public TaskExecutor eventTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.afterPropertiesSet();
        return executor;
    }

    @Bean
    public EventExceptionHandler eventExceptionHandler() {
        return new EventExceptionHandler();
    }

    @Bean
    public EventBus eventBus(TaskExecutor eventTaskExecutor, EventExceptionHandler eventExceptionHandler) {
        return new AsyncEventBus(eventTaskExecutor, eventExceptionHandler);
    }

    @Bean(destroyMethod = "close")
    public SearchEventListener eventListener(EventBus eventBus, QuestionSearchService questionSearchService) {
        return new SearchEventListener(eventBus, questionSearchService);
    }
}
