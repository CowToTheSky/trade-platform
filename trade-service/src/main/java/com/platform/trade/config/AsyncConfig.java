package com.platform.trade.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步配置类
 * 配置Spring异步任务执行器
 * 
 * @author Trade Platform Team
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {
    
    /**
     * 配置异步任务执行器
     * 用于Spring @Async注解的默认执行器
     * 
     * @return 线程池执行器
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 核心线程数
        executor.setCorePoolSize(8);
        
        // 最大线程数
        executor.setMaxPoolSize(16);
        
        // 队列容量
        executor.setQueueCapacity(2000);
        
        // 线程名前缀
        executor.setThreadNamePrefix("Async-Trade-");
        
        // 空闲线程存活时间（秒）
        executor.setKeepAliveSeconds(60);
        
        // 拒绝策略：调用者运行策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // 等待时间（秒）
        executor.setAwaitTerminationSeconds(60);
        
        // 初始化
        executor.initialize();
        
        log.info("异步任务执行器配置完成：核心线程数={}, 最大线程数={}, 队列容量={}", 
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
        
        return executor;
    }
    
    /**
     * 配置高频交易专用执行器
     * 用于处理高频交易场景
     * 
     * @return 高频交易执行器
     */
    @Bean(name = "highFrequencyExecutor")
    public Executor highFrequencyExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 更高的线程数配置，适用于高频交易
        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(32);
        executor.setQueueCapacity(5000);
        executor.setThreadNamePrefix("HighFreq-Trade-");
        executor.setKeepAliveSeconds(30);
        
        // 高频交易使用丢弃策略，避免阻塞
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        
        executor.initialize();
        
        log.info("高频交易执行器配置完成：核心线程数={}, 最大线程数={}, 队列容量={}", 
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
        
        return executor;
    }
}