package com.platform.trade.monitor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.platform.trade.async.AsyncOrderProcessor;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 性能监控组件
 * 监控异步订单处理的性能指标
 * 
 * @author Trade Platform Team
 */
@Slf4j
@Component
public class PerformanceMonitor {
    
    @Autowired
    private AsyncOrderProcessor asyncOrderProcessor;
    
    // 性能指标统计
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong totalProcessingTime = new AtomicLong(0);
    private final ConcurrentHashMap<String, AtomicLong> productProcessingCount = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> productProcessingTime = new ConcurrentHashMap<>();
    
    // 时间格式化器
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 记录订单处理开始
     * 
     * @param productCode 产品代码
     * @return 开始时间戳
     */
    public long recordProcessingStart(String productCode) {
        totalRequests.incrementAndGet();
        productProcessingCount.computeIfAbsent(productCode, k -> new AtomicLong(0)).incrementAndGet();
        return System.currentTimeMillis();
    }
    
    /**
     * 记录订单处理结束
     * 
     * @param productCode 产品代码
     * @param startTime 开始时间戳
     */
    public void recordProcessingEnd(String productCode, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        totalProcessingTime.addAndGet(duration);
        productProcessingTime.computeIfAbsent(productCode, k -> new AtomicLong(0)).addAndGet(duration);
        
        // 记录慢查询（超过1秒的处理）
        if (duration > 1000) {
            log.warn("慢订单处理检测：产品代码={}，处理时间={}ms", productCode, duration);
        }
    }
    
    /**
     * 获取平均处理时间
     * 
     * @return 平均处理时间（毫秒）
     */
    public double getAverageProcessingTime() {
        long requests = totalRequests.get();
        if (requests == 0) {
            return 0.0;
        }
        return (double) totalProcessingTime.get() / requests;
    }
    
    /**
     * 获取指定产品的平均处理时间
     * 
     * @param productCode 产品代码
     * @return 平均处理时间（毫秒）
     */
    public double getProductAverageProcessingTime(String productCode) {
        AtomicLong count = productProcessingCount.get(productCode);
        AtomicLong time = productProcessingTime.get(productCode);
        
        if (count == null || time == null || count.get() == 0) {
            return 0.0;
        }
        
        return (double) time.get() / count.get();
    }
    
    /**
     * 获取系统吞吐量（每秒处理订单数）
     * 基于最近一分钟的数据估算
     * 
     * @return 吞吐量
     */
    public double getThroughput() {
        // 简化实现：基于总请求数估算
        // 实际项目中可以使用滑动窗口来计算更精确的吞吐量
        long requests = totalRequests.get();
        if (requests == 0) {
            return 0.0;
        }
        
        // 假设系统运行时间，实际应该记录系统启动时间
        return requests / 60.0; // 简化为每分钟的平均值
    }
    
    /**
     * 获取性能报告
     * 
     * @return 性能报告字符串
     */
    public String getPerformanceReport() {
        StringBuilder report = new StringBuilder();
        report.append("\n=== 订单处理性能报告 ===");
        report.append("\n生成时间: ").append(LocalDateTime.now().format(formatter));
        report.append("\n总请求数: ").append(totalRequests.get());
        report.append("\n平均处理时间: ").append(String.format("%.2f", getAverageProcessingTime())).append("ms");
        report.append("\n估算吞吐量: ").append(String.format("%.2f", getThroughput())).append(" 订单/分钟");
        
        // 异步处理器状态
        report.append("\n\n=== 异步处理器状态 ===");
        report.append("\n").append(asyncOrderProcessor.getProcessingStats());
        report.append("\n").append(asyncOrderProcessor.getThreadPoolStatus());
        
        // 各产品处理统计
        if (!productProcessingCount.isEmpty()) {
            report.append("\n\n=== 各产品处理统计 ===");
            productProcessingCount.forEach((productCode, count) -> {
                double avgTime = getProductAverageProcessingTime(productCode);
                report.append(String.format("\n产品 %s: 处理次数=%d, 平均时间=%.2fms", 
                        productCode, count.get(), avgTime));
            });
        }
        
        return report.toString();
    }
    
    /**
     * 重置统计数据
     */
    public void resetStats() {
        totalRequests.set(0);
        totalProcessingTime.set(0);
        productProcessingCount.clear();
        productProcessingTime.clear();
        log.info("性能统计数据已重置");
    }
    
    /**
     * 定时输出性能报告
     * 每5分钟输出一次性能统计
     */
    @Scheduled(fixedRate = 300000) // 5分钟 = 300000毫秒
    public void scheduledPerformanceReport() {
        if (totalRequests.get() > 0) {
            log.info(getPerformanceReport());
        }
    }
    
    /**
     * 检查系统健康状态
     * 
     * @return 健康状态信息
     */
    public String getHealthStatus() {
        StringBuilder status = new StringBuilder();
        
        double avgTime = getAverageProcessingTime();
        double throughput = getThroughput();
        
        // 健康状态判断
        String healthLevel;
        if (avgTime < 100 && throughput > 10) {
            healthLevel = "优秀";
        } else if (avgTime < 500 && throughput > 5) {
            healthLevel = "良好";
        } else if (avgTime < 1000 && throughput > 1) {
            healthLevel = "一般";
        } else {
            healthLevel = "需要关注";
        }
        
        status.append("系统健康状态: ").append(healthLevel);
        status.append(" (平均处理时间: ").append(String.format("%.2f", avgTime)).append("ms, ");
        status.append("吞吐量: ").append(String.format("%.2f", throughput)).append(" 订单/分钟)");
        
        return status.toString();
    }
}