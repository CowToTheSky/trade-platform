-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `status` INT DEFAULT 1 COMMENT '用户状态（0-禁用，1-正常）',
    `role` VARCHAR(20) DEFAULT 'user' COMMENT '用户角色',
    `avatar_url` VARCHAR(255) COMMENT '头像URL',
    `last_login_ip` VARCHAR(50) COMMENT '上次登录IP',
    `last_login_time` DATETIME COMMENT '上次登录时间',
    `login_fail_count` INT DEFAULT 0 COMMENT '连续登录失败次数',
    `locked_until` DATETIME COMMENT '账户锁定截止时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_email` (`email`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表'; 