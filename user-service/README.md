# 用户服务模块

## MyBatis配置问题解决方案

### 问题描述
```
org.apache.ibatis.binding.BindingException: Invalid bound statement (not found): py.platform.user.mapper.UserMapper.insert
```

### 解决方案

#### 1. 确保数据库表存在
执行以下SQL创建用户表：
```sql
-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS trade_user DEFAULT CHARSET utf8mb4;

-- 使用数据库
USE trade_user;

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
```

#### 2. 检查配置文件
确保 `application.yml` 中的配置正确：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/trade_user?serverTimezone=Asia/Shanghai
    username: root
    password: 123456
  mybatis:
    mapper-locations: classpath:mapper/*.xml
    type-aliases-package: py.platform.user.model
    configuration:
      map-underscore-to-camel-case: true
      # 开启SQL日志
      log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

#### 3. 确保主启动类配置正确
主启动类必须添加 `@MapperScan` 注解：
```java
@SpringBootApplication
@MapperScan("py.platform.user.mapper")
public class UserServiceApplication {
    // ...
}
```

#### 4. 验证Mapper接口和XML文件
- Mapper接口：`py.platform.user.mapper.UserMapper`
- XML文件：`resources/mapper/UserMapper.xml`
- 确保namespace匹配：`py.platform.user.mapper.UserMapper`

#### 5. 启动应用
```bash
cd user-service
mvn spring-boot:run
```

### 常见问题排查

1. **数据库连接问题**：确保MySQL服务运行，数据库和表存在
2. **包路径问题**：确保Mapper接口在正确的包路径下
3. **XML文件位置**：确保XML文件在 `resources/mapper/` 目录下
4. **依赖问题**：确保pom.xml中包含MyBatis相关依赖

### 测试
启动应用后，控制台会显示MyBatis配置测试结果。如果看到"✓ MyBatis配置正常"，说明配置成功。 