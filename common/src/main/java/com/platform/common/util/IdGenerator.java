package com.platform.common.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * 全局唯一ID生成工具
 */
public class IdGenerator {
    // 可根据实际部署情况设置workerId和datacenterId
    private static final Snowflake snowflake = IdUtil.getSnowflake(1, 1);

    /**
     * 生成用户ID
     * @return 全局唯一用户ID
     */
    public static Long generateUserId() {
        return snowflake.nextId();
    }
} 