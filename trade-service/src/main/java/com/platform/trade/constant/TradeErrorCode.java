package com.platform.trade.constant;

/**
 * 交易错误码枚举
 * 
 * @author Trade Platform Team
 */
public enum TradeErrorCode {
    
    // 订单相关错误 (3001-3100)
    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_ALREADY_CANCELLED(3002, "订单已被撤销"),
    ORDER_ALREADY_FILLED(3003, "订单已完全成交"),
    ORDER_CANNOT_CANCEL(3004, "订单无法撤销"),
    INVALID_ORDER_TYPE(3005, "无效的订单类型"),
    INVALID_ORDER_QUANTITY(3006, "无效的订单数量"),
    INVALID_ORDER_PRICE(3007, "无效的订单价格"),
    ORDER_QUANTITY_TOO_SMALL(3008, "订单数量过小"),
    ORDER_PRICE_OUT_OF_RANGE(3009, "订单价格超出涨跌停范围"),
    
    // 产品相关错误 (3101-3200)
    PRODUCT_NOT_FOUND(3101, "产品不存在"),
    PRODUCT_SUSPENDED(3102, "产品已停牌"),
    PRODUCT_DELISTED(3103, "产品已退市"),
    PRODUCT_NOT_TRADABLE(3104, "产品不可交易"),
    
    // 资金相关错误 (3201-3300)
    INSUFFICIENT_BALANCE(3201, "账户余额不足"),
    INSUFFICIENT_POSITION(3202, "持仓数量不足"),
    ACCOUNT_FROZEN(3203, "账户已冻结"),
    FUND_OPERATION_FAILED(3204, "资金操作失败"),
    
    // 交易时间相关错误 (3301-3400)
    MARKET_CLOSED(3301, "市场已休市"),
    NOT_TRADING_TIME(3302, "非交易时间"),
    TRADING_SUSPENDED(3303, "交易暂停"),
    
    // 风控相关错误 (3401-3500)
    RISK_CONTROL_REJECT(3401, "风控拒绝"),
    DAILY_LIMIT_EXCEEDED(3402, "超出日交易限额"),
    POSITION_LIMIT_EXCEEDED(3403, "超出持仓限额"),
    FREQUENT_TRADING(3404, "交易过于频繁"),
    
    // 系统相关错误 (3501-3600)
    MATCHING_ENGINE_ERROR(3501, "撮合引擎错误"),
    SETTLEMENT_ERROR(3502, "结算错误"),
    DATA_INCONSISTENCY(3503, "数据不一致"),
    SYSTEM_MAINTENANCE(3504, "系统维护中");
    
    private final int code;
    private final String message;
    
    TradeErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}