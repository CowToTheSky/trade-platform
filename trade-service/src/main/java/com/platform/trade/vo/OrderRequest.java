package com.platform.trade.vo;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 下单请求参数
 * 
 * @author Trade Platform Team
 */
@Data
public class OrderRequest {
    
    /** 用户ID */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /** 产品代码 */
    @NotBlank(message = "产品代码不能为空")
    @Size(max = 20, message = "产品代码长度不能超过20个字符")
    private String productCode;
    
    /** 订单类型：1-买入，2-卖出 */
    @NotNull(message = "订单类型不能为空")
    @Min(value = 1, message = "订单类型必须为1或2")
    @Max(value = 2, message = "订单类型必须为1或2")
    private Integer orderType;
    
    /** 委托价格 */
    @NotNull(message = "委托价格不能为空")
    @DecimalMin(value = "0.01", message = "委托价格必须大于0.01")
    @Digits(integer = 10, fraction = 2, message = "价格格式不正确")
    private BigDecimal price;
    
    /** 委托数量 */
    @NotNull(message = "委托数量不能为空")
    @Min(value = 100, message = "委托数量不能少于100股")
    @Max(value = 1000000, message = "委托数量不能超过100万股")
    private Integer quantity;
    
    /** 订单来源：1-PC端，2-移动端，3-API */
    private Integer source = 1;
    
    /** 备注 */
    @Size(max = 200, message = "备注长度不能超过200个字符")
    private String remark;
}