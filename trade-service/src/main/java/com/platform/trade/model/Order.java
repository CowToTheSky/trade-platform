package com.platform.trade.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易订单实体类
 * 
 * @author Trade Platform Team
 */
@Data
public class Order {
    /** 订单ID */
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 产品代码 */
    private String productCode;
    
    /** 产品名称 */
    private String productName;
    
    /** 订单类型：1-买入，2-卖出 */
    private Integer orderType;
    
    /** 委托价格 */
    private BigDecimal price;
    
    /** 委托数量 */
    private Integer quantity;
    
    /** 已成交数量 */
    private Integer filledQuantity;
    
    /** 剩余数量 */
    private Integer remainingQuantity;
    
    /** 订单状态：1-待成交，2-部分成交，3-完全成交，4-已撤销 */
    private Integer status;
    
    /** 成交金额 */
    private BigDecimal filledAmount;
    
    /** 手续费 */
    private BigDecimal commission;
    
    /** 订单来源：1-PC端，2-移动端，3-API */
    private Integer source;
    
    /** 备注 */
    private String remark;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 撤销时间 */
    private LocalDateTime cancelledAt;
    
    /** 完成时间 */
    private LocalDateTime completedAt;
}