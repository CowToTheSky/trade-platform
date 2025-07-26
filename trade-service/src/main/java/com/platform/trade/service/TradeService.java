package com.platform.trade.service;

import com.platform.trade.vo.OrderRequest;
import com.platform.trade.vo.OrderResponse;
import com.github.pagehelper.PageInfo;
import java.util.List;

/**
 * 交易服务接口
 * 
 * @author Trade Platform Team
 */
public interface TradeService {
    
    /**
     * 提交订单
     * @param orderRequest 订单请求
     * @return 订单响应
     */
    OrderResponse submitOrder(OrderRequest orderRequest);
    
    /**
     * 撤销订单
     * @param orderId 订单ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否成功
     */
    boolean cancelOrder(Long orderId, Long userId);
    
    /**
     * 查询订单详情
     * @param orderId 订单ID
     * @param userId 用户ID（用于权限验证）
     * @return 订单详情
     */
    OrderResponse getOrderDetail(Long orderId, Long userId);
    
    /**
     * 查询用户订单列表
     * @param userId 用户ID
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页订单信息
     */
    PageInfo<OrderResponse> getUserOrders(Long userId, int page, int size);
    
    /**
     * 查询用户订单总数
     * @param userId 用户ID
     * @return 订单总数
     */
    int getUserOrderCount(Long userId);
    
    /**
     * 订单撮合处理（模拟撮合引擎）
     * @param productCode 产品代码
     */
    void matchOrders(String productCode);
}