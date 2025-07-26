# 交易服务模块 (Trade Service)

## 概述

交易服务模块是交易平台的核心业务模块，负责处理用户的交易订单、产品信息管理和订单撮合等功能。

## 主要功能

### 1. 订单管理
- **提交订单**: 用户可以提交买入或卖出订单
- **撤销订单**: 用户可以撤销未成交的订单
- **查询订单**: 支持订单详情查询和订单列表查询
- **订单状态**: 待成交、部分成交、完全成交、已撤销

### 2. 产品管理
- **产品查询**: 根据产品代码查询产品信息
- **产品列表**: 分页查询可交易产品列表
- **产品搜索**: 根据产品名称模糊搜索
- **产品分类**: 按产品类型（股票、基金、债券）查询

### 3. 交易撮合
- **价格优先**: 买单按价格降序，卖单按价格升序
- **时间优先**: 相同价格按时间先后顺序
- **自动撮合**: 系统自动进行订单匹配
- **部分成交**: 支持订单部分成交

## 技术架构

### 技术栈
- **Spring Boot 3.1.5**: 应用框架
- **MyBatis**: 数据访问层
- **MySQL**: 数据库
- **Swagger**: API文档
- **Lombok**: 代码简化
- **Jakarta Validation**: 参数校验

### 模块结构
```
trade-service/
├── src/main/java/com/platform/trade/
│   ├── controller/          # 控制器层
│   │   ├── TradeController.java
│   │   └── ProductController.java
│   ├── service/            # 服务层
│   │   ├── TradeService.java
│   │   ├── ProductService.java
│   │   └── impl/
│   ├── mapper/             # 数据访问层
│   │   ├── OrderMapper.java
│   │   └── ProductMapper.java
│   ├── model/              # 实体类
│   │   ├── Order.java
│   │   └── Product.java
│   ├── vo/                 # 视图对象
│   │   ├── OrderRequest.java
│   │   ├── OrderResponse.java
│   │   └── ProductResponse.java
│   ├── constant/           # 常量定义
│   │   ├── TradeConstants.java
│   │   └── TradeErrorCode.java
│   └── TradeServiceApplication.java
└── src/main/resources/
    └── application.properties
```

## API接口

### 交易接口

#### 1. 提交订单
```http
POST /api/trade/order
Content-Type: application/json

{
    "userId": 1,
    "productCode": "000001",
    "orderType": 1,
    "price": 12.50,
    "quantity": 1000,
    "source": 1,
    "remark": "买入平安银行"
}
```

#### 2. 撤销订单
```http
DELETE /api/trade/order/{orderId}?userId=1
```

#### 3. 查询订单详情
```http
GET /api/trade/order/{orderId}?userId=1
```

#### 4. 查询用户订单列表
```http
GET /api/trade/orders?userId=1&page=1&size=10
```

### 产品接口

#### 1. 查询产品信息
```http
GET /api/product/000001
```

#### 2. 查询产品列表
```http
GET /api/product/list?page=1&size=20
```

#### 3. 按类型查询产品
```http
GET /api/product/type/1?page=1&size=20
```

#### 4. 搜索产品
```http
GET /api/product/search?name=平安&page=1&size=20
```

## 数据库设计

### 主要表结构

#### 1. 产品表 (t_product)
- 存储产品基本信息、价格信息、交易规则等
- 支持股票、基金、债券等多种产品类型

#### 2. 订单表 (t_order)
- 存储用户交易订单信息
- 记录订单状态变化和成交情况

#### 3. 成交记录表 (t_trade_record)
- 记录订单撮合成交的详细信息
- 用于交易流水和对账

## 业务规则

### 1. 交易时间
- 上午: 09:30:00 - 11:30:00
- 下午: 13:00:00 - 15:00:00
- 非交易时间不允许提交订单

### 2. 订单规则
- 最小交易单位: 100股（1手）
- 订单数量必须是100的整数倍
- 价格必须在涨跌停范围内
- 价格精度: 保留2位小数

### 3. 手续费计算
- 手续费率: 万分之三 (0.0003)
- 最低手续费: 5元
- 买卖双方均收取手续费

### 4. 撮合规则
- 价格优先: 买单价格从高到低，卖单价格从低到高
- 时间优先: 相同价格按提交时间先后顺序
- 成交价格: 以卖方价格为准

## 启动说明

### 1. 数据库准备
```sql
-- 执行数据库初始化脚本
source sql/trade_init.sql
```

### 2. 配置文件
```properties
# application.properties
server.port=8082
spring.application.name=trade-service

# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/trade_platform
spring.datasource.username=root
spring.datasource.password=your_password
```

### 3. 启动服务
```bash
# 编译项目
mvn clean compile

# 启动服务
mvn spring-boot:run
```

### 4. 访问API文档
- Swagger UI: http://localhost:8082/swagger-ui.html
- API文档: http://localhost:8082/v3/api-docs

## 测试数据

系统已预置以下测试产品:
- 000001 - 平安银行 (股票)
- 000002 - 万科A (股票)
- 600036 - 招商银行 (股票)
- 600519 - 贵州茅台 (股票)
- 510050 - 50ETF (基金)

## 后续扩展

1. **实时行情**: 集成实时行情数据推送
2. **风控系统**: 添加交易风险控制规则
3. **持仓管理**: 实现用户持仓查询和管理
4. **资金管理**: 集成资金账户和结算功能
5. **消息通知**: 添加订单状态变化通知
6. **性能优化**: 引入缓存和异步处理