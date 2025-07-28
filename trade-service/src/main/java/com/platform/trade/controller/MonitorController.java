package com.platform.trade.controller;

import com.platform.trade.async.AsyncOrderProcessor;
import com.platform.trade.monitor.PerformanceMonitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 监控管理控制器
 * 提供性能监控和系统管理的API接口
 * 
 * @author Trade Platform Team
 */
@Slf4j
@RestController
@RequestMapping("/api/monitor")
@CrossOrigin(origins = "*")
public class MonitorController {
    
    @Autowired
    private PerformanceMonitor performanceMonitor;
    
    @Autowired
    private AsyncOrderProcessor asyncOrderProcessor;
    
    /**
     * 获取性能报告
     * 
     * @return 性能报告
     */
    @GetMapping("/performance")
    public ResponseEntity<Map<String, Object>> getPerformanceReport() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", performanceMonitor.getPerformanceReport());
            response.put("message", "性能报告获取成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取性能报告失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取性能报告失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 获取系统健康状态
     * 
     * @return 健康状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            
            Map<String, Object> healthData = new HashMap<>();
            healthData.put("status", performanceMonitor.getHealthStatus());
            healthData.put("averageProcessingTime", performanceMonitor.getAverageProcessingTime());
            healthData.put("throughput", performanceMonitor.getThroughput());
            healthData.put("asyncProcessorStats", asyncOrderProcessor.getProcessingStats());
            healthData.put("threadPoolStatus", asyncOrderProcessor.getThreadPoolStatus());
            
            response.put("data", healthData);
            response.put("message", "健康状态获取成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取健康状态失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取健康状态失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 重置性能统计数据
     * 
     * @return 操作结果
     */
    @PostMapping("/reset-stats")
    public ResponseEntity<Map<String, Object>> resetStats() {
        try {
            performanceMonitor.resetStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "性能统计数据重置成功");
            
            log.info("性能统计数据已通过API重置");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("重置性能统计数据失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "重置性能统计数据失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 手动触发订单撮合（用于测试）
     * 
     * @param productCode 产品代码
     * @return 操作结果
     */
    @PostMapping("/trigger-matching/{productCode}")
    public ResponseEntity<Map<String, Object>> triggerOrderMatching(@PathVariable String productCode) {
        try {
            asyncOrderProcessor.processOrderMatchingAsync(productCode)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error("手动触发撮合失败: productCode={}, error={}", 
                                productCode, throwable.getMessage());
                    } else {
                        log.info("手动触发撮合成功: productCode={}", productCode);
                    }
                });
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "订单撮合任务已提交，产品代码: " + productCode);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("触发订单撮合失败: productCode={}, error={}", productCode, e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "触发订单撮合失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 批量触发多个产品的订单撮合
     * 
     * @param productCodes 产品代码列表
     * @return 操作结果
     */
    @PostMapping("/batch-trigger-matching")
    public ResponseEntity<Map<String, Object>> batchTriggerOrderMatching(
            @RequestBody String[] productCodes) {
        try {
            if (productCodes == null || productCodes.length == 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "产品代码列表不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            asyncOrderProcessor.batchProcessOrderMatching(productCodes)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error("批量触发撮合失败: productCodes={}, error={}", 
                                String.join(",", productCodes), throwable.getMessage());
                    } else {
                        log.info("批量触发撮合成功: productCodes={}", String.join(",", productCodes));
                    }
                });
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("批量订单撮合任务已提交，共%d个产品", productCodes.length));
            response.put("data", productCodes);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("批量触发订单撮合失败: error={}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "批量触发订单撮合失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 获取异步处理器详细状态
     * 
     * @return 异步处理器状态
     */
    @GetMapping("/async-processor-status")
    public ResponseEntity<Map<String, Object>> getAsyncProcessorStatus() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            
            Map<String, Object> statusData = new HashMap<>();
            statusData.put("processingStats", asyncOrderProcessor.getProcessingStats());
            statusData.put("threadPoolStatus", asyncOrderProcessor.getThreadPoolStatus());
            
            response.put("data", statusData);
            response.put("message", "异步处理器状态获取成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取异步处理器状态失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取异步处理器状态失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}