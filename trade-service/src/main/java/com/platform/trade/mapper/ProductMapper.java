package com.platform.trade.mapper;

import com.platform.trade.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.github.pagehelper.Page;
import java.util.List;

/**
 * 产品数据访问层
 * 
 * @author Trade Platform Team
 */
@Mapper
public interface ProductMapper {
    
    /**
     * 根据产品代码查询产品信息
     * @param code 产品代码
     * @return 产品信息
     */
    Product selectByCode(@Param("code") String code);
    
    /**
     * 根据产品ID查询产品信息
     * @param id 产品ID
     * @return 产品信息
     */
    Product selectById(@Param("id") Long id);
    
    /**
     * 查询所有可交易的产品列表
     * @return 产品列表
     */
    Page<Product> selectTradableProducts();
    
    /**
     * 根据产品类型查询产品列表
     * @param type 产品类型
     * @return 产品列表
     */
    Page<Product> selectByType(@Param("type") Integer type);
    
    /**
     * 根据产品名称模糊查询
     * @param name 产品名称关键字
     * @return 产品列表
     */
    Page<Product> selectByNameLike(@Param("name") String name);
    
    /**
     * 查询可交易产品总数
     * @return 产品总数
     */
    int countTradableProducts();
    
    /**
     * 更新产品价格信息
     * @param code 产品代码
     * @param currentPrice 当前价格
     * @param volume 成交量
     * @param turnover 成交金额
     * @return 影响行数
     */
    int updatePriceInfo(@Param("code") String code,
                        @Param("currentPrice") java.math.BigDecimal currentPrice,
                        @Param("volume") Long volume,
                        @Param("turnover") java.math.BigDecimal turnover);
    
    /**
     * 更新产品状态
     * @param code 产品代码
     * @param status 新状态
     * @return 影响行数
     */
    int updateStatus(@Param("code") String code, @Param("status") Integer status);
    
    /**
     * 新增产品
     * @param product 产品信息
     * @return 影响行数
     */
    int insertProduct(Product product);
}