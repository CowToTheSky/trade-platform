package com.platform.trade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

/**
 * 交易服务启动类
 * 
 * @author Trade Platform Team
 */
@SpringBootApplication
@MapperScan("com.platform.trade.mapper")
@ComponentScan(basePackages = {"com.platform.trade", "com.platform.common"})
public class TradeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeServiceApplication.class, args);
    }

    /**
     * RestTemplate Bean配置
     * 用于API网关转发HTTP请求
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}