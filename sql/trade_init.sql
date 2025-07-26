-- 交易平台数据库初始化脚本
-- 创建交易相关表

-- 1. 产品信息表
CREATE TABLE IF NOT EXISTS `t_product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '产品ID',
    `code` VARCHAR(20) NOT NULL COMMENT '产品代码',
    `name` VARCHAR(100) NOT NULL COMMENT '产品名称',
    `type` INT NOT NULL DEFAULT 1 COMMENT '产品类型：1-股票，2-基金，3-债券',
    `current_price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '当前价格',
    `previous_close` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '昨日收盘价',
    `open_price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '今日开盘价',
    `high_price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '今日最高价',
    `low_price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '今日最低价',
    `volume` BIGINT NOT NULL DEFAULT 0 COMMENT '成交量',
    `turnover` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '成交金额',
    `change_percent` DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '涨跌幅(%)',
    `change_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '涨跌额',
    `market_value` DECIMAL(20,2) DEFAULT NULL COMMENT '市值',
    `circulating_shares` BIGINT DEFAULT NULL COMMENT '流通股本',
    `status` INT NOT NULL DEFAULT 1 COMMENT '产品状态：1-正常交易，2-停牌，3-退市',
    `trading_unit` INT NOT NULL DEFAULT 100 COMMENT '交易单位（每手股数）',
    `tick_size` DECIMAL(10,4) NOT NULL DEFAULT 0.01 COMMENT '最小价格变动单位',
    `upper_limit` DECIMAL(10,2) DEFAULT NULL COMMENT '涨停价',
    `lower_limit` DECIMAL(10,2) DEFAULT NULL COMMENT '跌停价',
    `description` TEXT COMMENT '产品描述',
    `listing_date` DATETIME DEFAULT NULL COMMENT '上市日期',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_product_code` (`code`),
    KEY `idx_product_type` (`type`),
    KEY `idx_product_status` (`status`),
    KEY `idx_product_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品信息表';

-- 2. 订单表
CREATE TABLE IF NOT EXISTS `t_order` (
    `id` BIGINT NOT NULL COMMENT '订单ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_code` VARCHAR(20) NOT NULL COMMENT '产品代码',
    `product_name` VARCHAR(100) NOT NULL COMMENT '产品名称',
    `order_type` INT NOT NULL COMMENT '订单类型：1-买入，2-卖出',
    `price` DECIMAL(10,2) NOT NULL COMMENT '委托价格',
    `quantity` INT NOT NULL COMMENT '委托数量',
    `filled_quantity` INT NOT NULL DEFAULT 0 COMMENT '已成交数量',
    `remaining_quantity` INT NOT NULL COMMENT '剩余数量',
    `status` INT NOT NULL DEFAULT 1 COMMENT '订单状态：1-待成交，2-部分成交，3-完全成交，4-已撤销',
    `filled_amount` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '成交金额',
    `commission` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '手续费',
    `source` INT NOT NULL DEFAULT 1 COMMENT '订单来源：1-PC端，2-移动端，3-API',
    `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `cancelled_at` DATETIME DEFAULT NULL COMMENT '撤销时间',
    `completed_at` DATETIME DEFAULT NULL COMMENT '完成时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_user_id` (`user_id`),
    KEY `idx_order_product_code` (`product_code`),
    KEY `idx_order_status` (`status`),
    KEY `idx_order_type` (`order_type`),
    KEY `idx_order_created_at` (`created_at`),
    KEY `idx_order_product_status` (`product_code`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 3. 成交记录表
CREATE TABLE IF NOT EXISTS `t_trade_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '成交记录ID',
    `buy_order_id` BIGINT NOT NULL COMMENT '买单ID',
    `sell_order_id` BIGINT NOT NULL COMMENT '卖单ID',
    `buy_user_id` BIGINT NOT NULL COMMENT '买方用户ID',
    `sell_user_id` BIGINT NOT NULL COMMENT '卖方用户ID',
    `product_code` VARCHAR(20) NOT NULL COMMENT '产品代码',
    `product_name` VARCHAR(100) NOT NULL COMMENT '产品名称',
    `price` DECIMAL(10,2) NOT NULL COMMENT '成交价格',
    `quantity` INT NOT NULL COMMENT '成交数量',
    `amount` DECIMAL(15,2) NOT NULL COMMENT '成交金额',
    `buy_commission` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '买方手续费',
    `sell_commission` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '卖方手续费',
    `trade_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '成交时间',
    PRIMARY KEY (`id`),
    KEY `idx_trade_buy_order` (`buy_order_id`),
    KEY `idx_trade_sell_order` (`sell_order_id`),
    KEY `idx_trade_buy_user` (`buy_user_id`),
    KEY `idx_trade_sell_user` (`sell_user_id`),
    KEY `idx_trade_product_code` (`product_code`),
    KEY `idx_trade_time` (`trade_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成交记录表';

-- 插入测试产品数据
INSERT INTO `t_product` (`code`, `name`, `type`, `current_price`, `previous_close`, `open_price`, 
                        `high_price`, `low_price`, `volume`, `turnover`, `change_percent`, `change_amount`,
                        `market_value`, `circulating_shares`, `status`, `trading_unit`, `tick_size`, 
                        `upper_limit`, `lower_limit`, `description`) VALUES
('000001', '平安银行', 1, 12.50, 12.30, 12.35, 12.68, 12.20, 15680000, 195840000.00, 1.63, 0.20, 
 125000000000.00, 10000000000, 1, 100, 0.01, 13.53, 11.07, '中国平安银行股份有限公司'),
('000002', '万科A', 1, 18.90, 18.75, 18.80, 19.20, 18.65, 8920000, 168588000.00, 0.80, 0.15, 
 189000000000.00, 10000000000, 1, 100, 0.01, 20.63, 16.88, '万科企业股份有限公司'),
('600036', '招商银行', 1, 35.60, 35.20, 35.30, 36.10, 35.15, 12450000, 443220000.00, 1.14, 0.40, 
 356000000000.00, 10000000000, 1, 100, 0.01, 38.72, 31.68, '招商银行股份有限公司'),
('600519', '贵州茅台', 1, 1680.00, 1665.00, 1670.00, 1695.00, 1665.00, 890000, 1495200000.00, 0.90, 15.00, 
 1680000000000.00, 1000000000, 1, 100, 0.01, 1831.50, 1498.50, '贵州茅台酒股份有限公司'),
('510050', '50ETF', 2, 2.850, 2.835, 2.840, 2.865, 2.830, 25600000, 72960000.00, 0.53, 0.015, 
 28500000000.00, 10000000000, 1, 100, 0.001, 3.119, 2.552, '华夏上证50ETF基金');

-- 创建索引优化查询性能
CREATE INDEX idx_product_code_status ON t_product(code, status);
CREATE INDEX idx_order_user_status ON t_order(user_id, status);
CREATE INDEX idx_order_product_type_status ON t_order(product_code, order_type, status);