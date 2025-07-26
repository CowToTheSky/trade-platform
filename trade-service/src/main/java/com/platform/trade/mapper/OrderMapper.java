package com.platform.trade.mapper;

import com.platform.trade.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.github.pagehelper.Page;

import java.util.List;

/**
 * 订单数据访问层
 * 
 * @author Trade Platform Team
 */
@Mapper
public interface OrderMapper {
    
    /**
     * 新增订单
     * @param order 订单信息
     * @return 影响行数
     */
    int insertOrder(Order order);
    
    /**
     * 根据订单ID查询订单
     * @param orderId 订单ID
     * @return 订单信息
     */
    Order selectByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据用户ID查询订单列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 订单列表
     */
    Page<Order> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户订单总数
     * @param userId 用户ID
     * @return 订单总数
     */
    int countByUserId(@Param("userId") Long userId);
    
    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param status 新状态
     * @param filledQuantity 已成交数量
     * @param remainingQuantity 剩余数量
     * @param filledAmount 成交金额
     * @return 影响行数
     */
    int updateOrderStatus(@Param("orderId") Long orderId,
                          @Param("status") Integer status,
                          @Param("filledQuantity") Integer filledQuantity,
                          @Param("remainingQuantity") Integer remainingQuantity,
                          @Param("filledAmount") java.math.BigDecimal filledAmount);
    
    /**
     * 撤销订单
     * @param orderId 订单ID
     * @return 影响行数
     */
    int cancelOrder(@Param("orderId") Long orderId);
    
    /**
     * 查询待成交和部分成交的订单
     * @param productCode 产品代码
     * @param orderType 订单类型
     * @return 订单列表
     */
    List<Order> selectPendingOrders(@Param("productCode") String productCode,
                                    @Param("orderType") Integer orderType);
    
    /**
     * 根据订单ID和用户ID查询订单（用于权限验证）
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 订单信息
     */
    Order selectByOrderIdAndUserId(@Param("orderId") Long orderId, 
                                   @Param("userId") Long userId);
}