package com.platform.trade.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产品信息实体类
 * 
 * @author Trade Platform Team
 */
@Data
public class Product {
    /** 产品ID */
    private Long id;
    
    /** 产品代码 */
    private String code;
    
    /** 产品名称 */
    private String name;
    
    /** 产品类型：1-股票，2-基金，3-债券 */
    private Integer type;
    
    /** 当前价格 */
    private BigDecimal currentPrice;
    
    /** 昨日收盘价 */
    private BigDecimal previousClose;
    
    /** 今日开盘价 */
    private BigDecimal openPrice;
    
    /** 今日最高价 */
    private BigDecimal highPrice;
    
    /** 今日最低价 */
    private BigDecimal lowPrice;
    
    /** 成交量 */
    private Long volume;
    
    /** 成交金额 */
    private BigDecimal turnover;
    
    /** 涨跌幅 */
    private BigDecimal changePercent;
    
    /** 涨跌额 */
    private BigDecimal changeAmount;
    
    /** 市值 */
    private BigDecimal marketValue;
    
    /** 流通股本 */
    private Long circulatingShares;
    
    /** 产品状态：1-正常交易，2-停牌，3-退市 */
    private Integer status;
    
    /** 交易单位（每手股数） */
    private Integer tradingUnit;
    
    /** 最小价格变动单位 */
    private BigDecimal tickSize;
    
    /** 涨停价 */
    private BigDecimal upperLimit;
    
    /** 跌停价 */
    private BigDecimal lowerLimit;
    
    /** 产品描述 */
    private String description;
    
    /** 上市日期 */
    private LocalDateTime listingDate;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
}