package com.platform.trade.constant;

/**
 * 交易相关常量
 * 
 * @author Trade Platform Team
 */
public class TradeConstants {
    
    /** 订单类型 */
    public static final int ORDER_TYPE_BUY = 1;  // 买入
    public static final int ORDER_TYPE_SELL = 2; // 卖出
    
    /** 订单状态 */
    public static final int ORDER_STATUS_PENDING = 1;    // 待成交
    public static final int ORDER_STATUS_PARTIAL = 2;    // 部分成交
    public static final int ORDER_STATUS_FILLED = 3;     // 完全成交
    public static final int ORDER_STATUS_CANCELLED = 4;  // 已撤销
    
    /** 产品类型 */
    public static final int PRODUCT_TYPE_STOCK = 1; // 股票
    public static final int PRODUCT_TYPE_FUND = 2;  // 基金
    public static final int PRODUCT_TYPE_BOND = 3;  // 债券
    
    /** 产品状态 */
    public static final int PRODUCT_STATUS_TRADING = 1; // 正常交易
    public static final int PRODUCT_STATUS_SUSPENDED = 2; // 停牌
    public static final int PRODUCT_STATUS_DELISTED = 3; // 退市
    
    /** 订单来源 */
    public static final int ORDER_SOURCE_PC = 1;     // PC端
    public static final int ORDER_SOURCE_MOBILE = 2; // 移动端
    public static final int ORDER_SOURCE_API = 3;    // API
    
    /** 交易时间段 */
    public static final String TRADING_MORNING_START = "09:30:00";
    public static final String TRADING_MORNING_END = "11:30:00";
    public static final String TRADING_AFTERNOON_START = "13:00:00";
    public static final String TRADING_AFTERNOON_END = "15:00:00";
    
    /** 手续费率 */
    public static final double COMMISSION_RATE = 0.0003; // 万分之三
    public static final double MIN_COMMISSION = 5.0;     // 最低手续费5元
    
    /** 交易单位 */
    public static final int DEFAULT_TRADING_UNIT = 100; // 默认每手100股
    
    /** 价格精度 */
    public static final int PRICE_SCALE = 2; // 价格保留2位小数
}