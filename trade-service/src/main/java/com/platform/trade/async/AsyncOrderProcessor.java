package com.platform.trade.async;

import com.platform.trade.service.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 异步订单处理器
 * 用于高并发场景下的订单异步处理和撮合
 * 
 * @author Trade Platform Team
 */
@Slf4j
@Component
public class AsyncOrderProcessor {
    
    @Autowired
    private TradeService tradeService;
    
    // 订单处理计数器
    private final AtomicLong processedOrderCount = new AtomicLong(0);
    private final AtomicLong failedOrderCount = new AtomicLong(0);
    
    // 自定义线程池用于订单处理
    private final ThreadPoolExecutor orderProcessingPool;
    
    public AsyncOrderProcessor() {
        // 创建专用的订单处理线程池
        // 核心线程数：4，最大线程数：8，队列容量：1000
        this.orderProcessingPool = new ThreadPoolExecutor(
            4, // 核心线程数
            8, // 最大线程数
            60L, // 空闲线程存活时间
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000), // 阻塞队列
            r -> {
                Thread thread = new Thread(r, "OrderProcessor-" + System.currentTimeMillis());
                thread.setDaemon(false);
                return thread;
            },
            new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略：调用者运行
        );
        
        log.info("异步订单处理器初始化完成，线程池配置：核心线程数={}, 最大线程数={}, 队列容量={}", 
                4, 8, 1000);
    }
    
    /**
     * 异步处理订单撮合
     * 
     * @param productCode 产品代码
     * @return CompletableFuture<Void>
     */
    @Async
    public CompletableFuture<Void> processOrderMatchingAsync(String productCode) {
        return CompletableFuture.runAsync(() -> {
            long startTime = System.currentTimeMillis();
            try {
                log.info("开始异步处理订单撮合，产品代码：{}", productCode);
                
                // 调用撮合服务
                tradeService.matchOrders(productCode);
                
                // 增加处理计数
                long count = processedOrderCount.incrementAndGet();
                long duration = System.currentTimeMillis() - startTime;
                
                log.info("订单撮合处理完成，产品代码：{}，耗时：{}ms，累计处理：{}笔", 
                        productCode, duration, count);
                
            } catch (Exception e) {
                long failedCount = failedOrderCount.incrementAndGet();
                long duration = System.currentTimeMillis() - startTime;
                
                log.error("订单撮合处理失败，产品代码：{}，耗时：{}ms，累计失败：{}笔，错误信息：{}", 
                        productCode, duration, failedCount, e.getMessage(), e);
                
                // 可以在这里添加重试逻辑或者发送告警
                handleProcessingFailure(productCode, e);
            }
        }, orderProcessingPool);
    }
    
    /**
     * 批量异步处理多个产品的订单撮合
     * 
     * @param productCodes 产品代码列表
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> batchProcessOrderMatching(String... productCodes) {
        CompletableFuture<Void>[] futures = new CompletableFuture[productCodes.length];
        
        for (int i = 0; i < productCodes.length; i++) {
            futures[i] = processOrderMatchingAsync(productCodes[i]);
        }
        
        return CompletableFuture.allOf(futures);
    }
    
    /**
     * 处理失败的订单撮合
     * 
     * @param productCode 产品代码
     * @param exception 异常信息
     */
    private void handleProcessingFailure(String productCode, Exception exception) {
        // 这里可以实现重试逻辑、告警通知等
        log.warn("订单撮合失败处理：产品代码={}，将在30秒后重试", productCode);
        
        // 延迟重试（简单实现，实际项目中可以使用更复杂的重试机制）
        CompletableFuture.delayedExecutor(30, TimeUnit.SECONDS)
                .execute(() -> {
                    try {
                        log.info("重试订单撮合：产品代码={}", productCode);
                        tradeService.matchOrders(productCode);
                        log.info("重试订单撮合成功：产品代码={}", productCode);
                    } catch (Exception retryException) {
                        log.error("重试订单撮合仍然失败：产品代码={}，错误信息：{}", 
                                productCode, retryException.getMessage());
                    }
                });
    }
    
    /**
     * 获取处理统计信息
     * 
     * @return 统计信息字符串
     */
    public String getProcessingStats() {
        return String.format("订单处理统计 - 成功：%d笔，失败：%d笔，线程池状态：活跃线程=%d，队列大小=%d",
                processedOrderCount.get(),
                failedOrderCount.get(),
                orderProcessingPool.getActiveCount(),
                orderProcessingPool.getQueue().size());
    }
    
    /**
     * 获取线程池状态
     * 
     * @return 线程池状态信息
     */
    public String getThreadPoolStatus() {
        return String.format("线程池状态 - 核心线程数：%d，最大线程数：%d，当前线程数：%d，活跃线程数：%d，" +
                        "已完成任务数：%d，队列中任务数：%d，队列剩余容量：%d",
                orderProcessingPool.getCorePoolSize(),
                orderProcessingPool.getMaximumPoolSize(),
                orderProcessingPool.getPoolSize(),
                orderProcessingPool.getActiveCount(),
                orderProcessingPool.getCompletedTaskCount(),
                orderProcessingPool.getQueue().size(),
                orderProcessingPool.getQueue().remainingCapacity());
    }
    
    /**
     * 优雅关闭线程池
     */
    public void shutdown() {
        log.info("开始关闭异步订单处理器...");
        orderProcessingPool.shutdown();
        try {
            if (!orderProcessingPool.awaitTermination(60, TimeUnit.SECONDS)) {
                orderProcessingPool.shutdownNow();
                if (!orderProcessingPool.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.error("线程池无法正常关闭");
                }
            }
        } catch (InterruptedException e) {
            orderProcessingPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("异步订单处理器已关闭");
    }
}