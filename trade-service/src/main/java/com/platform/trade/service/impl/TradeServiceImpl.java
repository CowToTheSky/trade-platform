package com.platform.trade.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.platform.trade.async.AsyncOrderProcessor;
import com.platform.trade.constant.TradeConstants;
import com.platform.trade.constant.TradeErrorCode;
import com.platform.trade.mapper.OrderMapper;
import com.platform.trade.model.Order;
import com.platform.trade.service.OrderMatchingService;
import com.platform.trade.service.ProductService;
import com.platform.trade.service.TradeService;
import com.platform.trade.vo.OrderRequest;
import com.platform.trade.vo.OrderResponse;
import com.platform.trade.vo.ProductResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * 交易服务实现类
 * 
 * @author Trade Platform Team
 */
@Slf4j
@Service
public class TradeServiceImpl implements TradeService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private AsyncOrderProcessor asyncOrderProcessor;
    
    @Autowired
    private OrderMatchingService orderMatchingService;
    
    @Override
    @Transactional
    public OrderResponse submitOrder(OrderRequest orderRequest) {
        log.info("提交订单请求: {}", orderRequest);
        
        // 1. 验证交易时间
        if (!isTradingTime()) {
            throw new RuntimeException(TradeErrorCode.NOT_TRADING_TIME.getMessage());
        }
        
        // 2. 验证产品信息
        ProductResponse product = productService.getProductByCode(orderRequest.getProductCode());
        if (product == null) {
            throw new RuntimeException(TradeErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }
        
        // 3. 检查产品是否可交易
        if (!productService.isProductTradable(orderRequest.getProductCode())) {
            throw new RuntimeException(TradeErrorCode.PRODUCT_NOT_TRADABLE.getMessage());
        }
        
        // 4. 验证价格范围
        if (!productService.isPriceValid(orderRequest.getProductCode(), orderRequest.getPrice())) {
            throw new RuntimeException(TradeErrorCode.ORDER_PRICE_OUT_OF_RANGE.getMessage());
        }
        
        // 5. 验证订单数量
        if (orderRequest.getQuantity() % TradeConstants.DEFAULT_TRADING_UNIT != 0) {
            throw new RuntimeException(TradeErrorCode.ORDER_QUANTITY_TOO_SMALL.getMessage());
        }
        
        // 6. 创建订单
        Order order = createOrder(orderRequest, product);
        
        // 7. 保存订单
        int result = orderMapper.insertOrder(order);
        if (result <= 0) {
            throw new RuntimeException("订单保存失败");
        }
        
        // 8. 触发撮合（异步处理）
        try {
            // 使用异步订单处理器进行撮合
            asyncOrderProcessor.processOrderMatchingAsync(orderRequest.getProductCode())
                .whenComplete((voidResult, throwable) -> {
                    if (throwable != null) {
                        log.error("异步撮合处理失败: productCode={}, error={}", 
                                orderRequest.getProductCode(), throwable.getMessage(), throwable);
                    } else {
                        log.info("异步撮合处理已提交: productCode={}", orderRequest.getProductCode());
                    }
                });
        } catch (Exception e) {
            log.error("提交异步撮合任务失败: {}", e.getMessage(), e);
            // 如果异步处理失败，降级为同步处理
            try {
                matchOrders(orderRequest.getProductCode());
            } catch (Exception syncException) {
                log.error("同步撮合处理也失败: {}", syncException.getMessage(), syncException);
            }
        }
        
        // 9. 返回订单响应
        return convertToOrderResponse(order);
    }
    
    @Override
    @Transactional
    public boolean cancelOrder(Long orderId, Long userId) {
        log.info("撤销订单请求: orderId={}, userId={}", orderId, userId);
        
        // 1. 查询订单
        Order order = orderMapper.selectByOrderIdAndUserId(orderId, userId);
        if (order == null) {
            throw new RuntimeException(TradeErrorCode.ORDER_NOT_FOUND.getMessage());
        }
        
        // 2. 检查订单状态
        if (order.getStatus().equals(TradeConstants.ORDER_STATUS_FILLED)) {
            throw new RuntimeException(TradeErrorCode.ORDER_ALREADY_FILLED.getMessage());
        }
        
        if (order.getStatus().equals(TradeConstants.ORDER_STATUS_CANCELLED)) {
            throw new RuntimeException(TradeErrorCode.ORDER_ALREADY_CANCELLED.getMessage());
        }
        
        // 3. 撤销订单
        int result = orderMapper.cancelOrder(orderId);
        return result > 0;
    }
    
    @Override
    public OrderResponse getOrderDetail(Long orderId, Long userId) {
        Order order = orderMapper.selectByOrderIdAndUserId(orderId, userId);
        if (order == null) {
            throw new RuntimeException(TradeErrorCode.ORDER_NOT_FOUND.getMessage());
        }
        return convertToOrderResponse(order);
    }
    
    @Override
    public PageInfo<OrderResponse> getUserOrders(Long userId, int page, int size) {
        PageHelper.startPage(page, size);
        List<Order> orders = orderMapper.selectByUserId(userId);
        List<OrderResponse> orderResponses = orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
        return new PageInfo<>(orderResponses);
    }
    
    @Override
    public int getUserOrderCount(Long userId) {
        return orderMapper.countByUserId(userId);
    }
    
    @Override
    @Transactional
    public void matchOrders(String productCode) {
        // 委托给专门的撮合服务执行
        orderMatchingService.executeMatching(productCode);
    }
    
    /**
     * 创建订单对象
     */
    private Order createOrder(OrderRequest request, ProductResponse product) {
        Order order = new Order();
        order.setId(generateOrderId());
        order.setUserId(request.getUserId());
        order.setProductCode(request.getProductCode());
        order.setProductName(product.getName());
        order.setOrderType(request.getOrderType());
        order.setPrice(request.getPrice());
        order.setQuantity(request.getQuantity());
        order.setFilledQuantity(0);
        order.setRemainingQuantity(request.getQuantity());
        order.setStatus(TradeConstants.ORDER_STATUS_PENDING);
        order.setFilledAmount(BigDecimal.ZERO);
        order.setCommission(calculateCommission(request.getPrice(), request.getQuantity()));
        order.setSource(request.getSource());
        order.setRemark(request.getRemark());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        return order;
    }
    
    /**
     * 生成订单ID
     */
    private Long generateOrderId() {
        return System.currentTimeMillis() + (long)(Math.random() * 1000);
    }
    
    /**
     * 计算手续费
     */
    private BigDecimal calculateCommission(BigDecimal price, Integer quantity) {
        BigDecimal amount = price.multiply(BigDecimal.valueOf(quantity));
        BigDecimal commission = amount.multiply(BigDecimal.valueOf(TradeConstants.COMMISSION_RATE));
        
        // 最低手续费
        BigDecimal minCommission = BigDecimal.valueOf(TradeConstants.MIN_COMMISSION);
        return commission.compareTo(minCommission) < 0 ? minCommission : commission;
    }
    
    /**
     * 检查是否在交易时间内
     * 注意：为了演示方便，暂时允许全天交易
     */
    private boolean isTradingTime() {
        // 暂时允许全天交易，便于测试
        return true;
        
        // 原有的交易时间限制逻辑（已注释）
        /*
        LocalTime now = LocalTime.now();
        LocalTime morningStart = LocalTime.parse(TradeConstants.TRADING_MORNING_START);
        LocalTime morningEnd = LocalTime.parse(TradeConstants.TRADING_MORNING_END);
        LocalTime afternoonStart = LocalTime.parse(TradeConstants.TRADING_AFTERNOON_START);
        LocalTime afternoonEnd = LocalTime.parse(TradeConstants.TRADING_AFTERNOON_END);
        
        return (now.isAfter(morningStart) && now.isBefore(morningEnd)) ||
               (now.isAfter(afternoonStart) && now.isBefore(afternoonEnd));
        */
    }
    
    /**
     * 更新订单成交信息
     */
    private void updateOrderAfterMatch(Order order, int matchQuantity, BigDecimal matchPrice) {
        int newFilledQuantity = order.getFilledQuantity() + matchQuantity;
        int newRemainingQuantity = order.getRemainingQuantity() - matchQuantity;
        
        BigDecimal matchAmount = matchPrice.multiply(BigDecimal.valueOf(matchQuantity));
        BigDecimal newFilledAmount = order.getFilledAmount().add(matchAmount);
        
        int newStatus = newRemainingQuantity > 0 ? 
                TradeConstants.ORDER_STATUS_PARTIAL : TradeConstants.ORDER_STATUS_FILLED;
        
        orderMapper.updateOrderStatus(order.getId(), newStatus, 
                newFilledQuantity, newRemainingQuantity, newFilledAmount);
        
        // 更新内存中的订单对象
        order.setFilledQuantity(newFilledQuantity);
        order.setRemainingQuantity(newRemainingQuantity);
        order.setFilledAmount(newFilledAmount);
        order.setStatus(newStatus);
    }
    
    /**
     * 转换为订单响应对象
     */
    private OrderResponse convertToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        BeanUtils.copyProperties(order, response);
        response.setOrderId(order.getId());
        
        // 设置描述信息
        response.setOrderTypeDesc(order.getOrderType().equals(TradeConstants.ORDER_TYPE_BUY) ? "买入" : "卖出");
        
        return response;
    }
}