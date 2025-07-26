package com.platform.trade.controller;

import com.platform.common.vo.ResponseVO;
import com.platform.trade.vo.ProductResponse;
import com.platform.trade.service.TradeService;
import com.platform.trade.vo.OrderRequest;
import com.platform.trade.vo.OrderResponse;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易控制器
 * 
 * @author Trade Platform Team
 */
@Slf4j
@RestController
@RequestMapping("/api/trade")
@Tag(name = "交易管理", description = "交易相关接口")
@Validated
public class TradeController {
    
    @Autowired
    private TradeService tradeService;
    
    @Autowired
    private com.platform.trade.service.ProductService productService;
    
    /**
     * 提交订单
     */
    @PostMapping("/orders")
    @Operation(summary = "提交订单", description = "用户提交买入或卖出订单")
    public ResponseVO<OrderResponse> submitOrder(
            @Valid @RequestBody OrderRequest orderRequest) {
        try {
            log.info("提交订单请求: {}", orderRequest);
            OrderResponse orderResponse = tradeService.submitOrder(orderRequest);
            return ResponseVO.success(orderResponse);
        } catch (Exception e) {
            log.error("提交订单失败: {}", e.getMessage(), e);
            return ResponseVO.error(500, e.getMessage());
        }
    }
    
    /**
     * 撤销订单
     */
    @PutMapping("/orders/{orderId}/cancel")
    @Operation(summary = "撤销订单", description = "用户撤销未成交的订单")
    public ResponseVO<Boolean> cancelOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "用户ID") @RequestParam @NotNull Long userId) {
        try {
            log.info("撤销订单请求: orderId={}, userId={}", orderId, userId);
            boolean result = tradeService.cancelOrder(orderId, userId);
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("撤销订单失败: {}", e.getMessage(), e);
            return ResponseVO.error(500, e.getMessage());
        }
    }
    
    /**
     * 查询订单详情
     */
    @GetMapping("/order/{orderId}")
    @Operation(summary = "查询订单详情", description = "根据订单ID查询订单详细信息")
    public ResponseVO<OrderResponse> getOrderDetail(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "用户ID") @RequestParam @NotNull Long userId) {
        try {
            log.info("查询订单详情: orderId={}, userId={}", orderId, userId);
            OrderResponse orderResponse = tradeService.getOrderDetail(orderId, userId);
            return ResponseVO.success(orderResponse);
        } catch (Exception e) {
            log.error("查询订单详情失败: {}", e.getMessage(), e);
            return ResponseVO.error(500, e.getMessage());
        }
    }
    
    /**
     * 查询用户订单列表
     */
    @GetMapping("/orders/user/{userId}")
    @Operation(summary = "查询用户订单列表", description = "分页查询用户的订单列表")
    public ResponseVO<PageInfo<OrderResponse>> getUserOrders(
            @Parameter(description = "用户ID") @PathVariable @NotNull Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) int size) {
        try {
            log.info("查询用户订单列表: userId={}, page={}, size={}", userId, page, size);
            
            PageInfo<OrderResponse> pageInfo = tradeService.getUserOrders(userId, page, size);
            
            return ResponseVO.success(pageInfo);
        } catch (Exception e) {
            log.error("查询用户订单列表失败: {}", e.getMessage(), e);
            return ResponseVO.error(500, e.getMessage());
        }
    }
    
    /**
     * 手动触发撮合（测试用）
     */
    @PostMapping("/match/{productCode}")
    @Operation(summary = "手动触发撮合", description = "手动触发指定产品的订单撮合（测试用）")
    public ResponseVO<String> triggerMatch(
            @Parameter(description = "产品代码") @PathVariable String productCode) {
        try {
            log.info("手动触发撮合: productCode={}", productCode);
            tradeService.matchOrders(productCode);
            return ResponseVO.success("撮合触发成功");
        } catch (Exception e) {
            log.error("触发撮合失败: {}", e.getMessage(), e);
            return ResponseVO.error(500, e.getMessage());
        }
    }
    
    /**
     * 查询可交易产品列表
     */
    @GetMapping("/products/tradable")
    @Operation(summary = "查询可交易产品列表", description = "分页查询所有可交易的产品列表")
    public ResponseVO<PageInfo<ProductResponse>> getTradableProducts(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") @Min(1) int size) {
        try {
            log.info("查询可交易产品列表: page={}, size={}", page, size);
            
            PageInfo<ProductResponse> pageInfo = productService.getTradableProducts(page, size);
            
            return ResponseVO.success(pageInfo);
        } catch (Exception e) {
            log.error("查询可交易产品列表失败: {}", e.getMessage(), e);
            return ResponseVO.error(500, e.getMessage());
        }
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查交易服务是否正常运行")
    public ResponseVO<String> health() {
        return ResponseVO.success("交易服务运行正常");
    }
}