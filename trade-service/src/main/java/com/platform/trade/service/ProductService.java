package com.platform.trade.service;

import com.github.pagehelper.PageInfo;
import com.platform.trade.vo.ProductResponse;

/**
 * 产品服务接口
 * 
 * @author Trade Platform Team
 */
public interface ProductService {
    
    /**
     * 根据产品代码查询产品信息
     * @param code 产品代码
     * @return 产品信息
     */
    ProductResponse getProductByCode(String code);
    
    /**
     * 查询所有可交易的产品列表
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页产品信息
     */
    PageInfo<ProductResponse> getTradableProducts(int page, int size);
    
    /**
     * 根据产品类型查询产品列表
     * @param type 产品类型：1-股票，2-基金，3-债券
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页产品信息
     */
    PageInfo<ProductResponse> getProductsByType(Integer type, int page, int size);
    
    /**
     * 根据产品名称模糊查询
     * @param name 产品名称关键字
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页产品信息
     */
    PageInfo<ProductResponse> searchProductsByName(String name, int page, int size);
    
    /**
     * 查询可交易产品总数
     * @return 产品总数
     */
    int getTradableProductCount();
    
    /**
     * 检查产品是否可交易
     * @param code 产品代码
     * @return 是否可交易
     */
    boolean isProductTradable(String code);
    
    /**
     * 验证订单价格是否在涨跌停范围内
     * @param code 产品代码
     * @param price 订单价格
     * @return 是否在有效范围内
     */
    boolean isPriceValid(String code, java.math.BigDecimal price);
}