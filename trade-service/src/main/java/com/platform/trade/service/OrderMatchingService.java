package com.platform.trade.service;

import com.platform.trade.constant.TradeConstants;
import com.platform.trade.mapper.OrderMapper;
import com.platform.trade.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单撮合服务
 * 独立的撮合逻辑，避免循环依赖
 * 
 * @author Trade Platform Team
 */
@Slf4j
@Service
public class OrderMatchingService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    /**
     * 执行订单撮合
     * 
     * @param productCode 产品代码
     */
    @Transactional
    public void executeMatching(String productCode) {
        log.info("开始撮合订单: productCode={}", productCode);
        
        // 简化的撮合逻辑：查找买卖双方订单进行匹配
        List<Order> buyOrders = orderMapper.selectPendingOrders(productCode, TradeConstants.ORDER_TYPE_BUY);
        List<Order> sellOrders = orderMapper.selectPendingOrders(productCode, TradeConstants.ORDER_TYPE_SELL);
        
        // 按价格排序：买单按价格降序，卖单按价格升序
        buyOrders.sort((o1, o2) -> o2.getPrice().compareTo(o1.getPrice()));
        sellOrders.sort((o1, o2) -> o1.getPrice().compareTo(o2.getPrice()));
        
        // 撮合逻辑
        for (Order buyOrder : buyOrders) {
            for (Order sellOrder : sellOrders) {
                if (buyOrder.getPrice().compareTo(sellOrder.getPrice()) >= 0) {
                    // 可以成交
                    int matchQuantity = Math.min(buyOrder.getRemainingQuantity(), sellOrder.getRemainingQuantity());
                    BigDecimal matchPrice = sellOrder.getPrice(); // 以卖价成交
                    
                    // 更新买单
                    updateOrderAfterMatch(buyOrder, matchQuantity, matchPrice);
                    
                    // 更新卖单
                    updateOrderAfterMatch(sellOrder, matchQuantity, matchPrice);
                    
                    log.info("订单撮合成功: 买单={}, 卖单={}, 成交数量={}, 成交价格={}", 
                            buyOrder.getId(), sellOrder.getId(), matchQuantity, matchPrice);
                    
                    // 如果卖单完全成交，跳出内层循环
                    if (sellOrder.getRemainingQuantity() <= 0) {
                        break;
                    }
                }
            }
        }
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
}