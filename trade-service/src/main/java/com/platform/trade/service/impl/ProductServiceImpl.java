package com.platform.trade.service.impl;

import com.platform.trade.constant.TradeConstants;
import com.platform.trade.mapper.ProductMapper;
import com.platform.trade.model.Product;
import com.platform.trade.service.ProductService;
import com.platform.trade.vo.ProductResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 产品服务实现类
 * 
 * @author Trade Platform Team
 */
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    public ProductResponse getProductByCode(String code) {
        Product product = productMapper.selectByCode(code);
        return product != null ? convertToProductResponse(product) : null;
    }
    
    @Override
    public PageInfo<ProductResponse> getTradableProducts(int page, int size) {
        PageHelper.startPage(page, size);
        List<Product> products = productMapper.selectTradableProducts();
        List<ProductResponse> responses = products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
        return new PageInfo<>(responses);
    }

    @Override
    public PageInfo<ProductResponse> getProductsByType(Integer type, int page, int size) {
        PageHelper.startPage(page, size);
        List<Product> products = productMapper.selectByType(type);
        List<ProductResponse> responses = products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
        return new PageInfo<>(responses);
    }

    @Override
    public PageInfo<ProductResponse> searchProductsByName(String name, int page, int size) {
        PageHelper.startPage(page, size);
        List<Product> products = productMapper.selectByNameLike(name);
        List<ProductResponse> responses = products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
        return new PageInfo<>(responses);
    }
    
    @Override
    public int getTradableProductCount() {
        return productMapper.countTradableProducts();
    }
    
    @Override
    public boolean isProductTradable(String code) {
        Product product = productMapper.selectByCode(code);
        if (product == null) {
            return false;
        }
        
        // 检查产品状态：1-正常交易
        return product.getStatus() != null && product.getStatus().equals(TradeConstants.PRODUCT_STATUS_TRADING);
    }
    
    @Override
    public boolean isPriceValid(String code, BigDecimal price) {
        Product product = productMapper.selectByCode(code);
        if (product == null) {
            return false;
        }
        
        // 检查价格是否在涨跌停范围内
        if (product.getUpperLimit() != null && price.compareTo(product.getUpperLimit()) > 0) {
            return false;
        }
        
        if (product.getLowerLimit() != null && price.compareTo(product.getLowerLimit()) < 0) {
            return false;
        }
        
        // 检查价格精度（最小变动单位）
        if (product.getTickSize() != null) {
            BigDecimal remainder = price.remainder(product.getTickSize());
            if (remainder.compareTo(BigDecimal.ZERO) != 0) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 转换为产品响应对象
     */
    private ProductResponse convertToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        BeanUtils.copyProperties(product, response);
        
        // 设置产品类型描述
        String typeDesc;
        if (product.getType() == null) {
            typeDesc = "未知类型";
        } else {
            switch (product.getType()) {
                case TradeConstants.PRODUCT_TYPE_STOCK:
                    typeDesc = "股票";
                    break;
                case TradeConstants.PRODUCT_TYPE_FUND:
                    typeDesc = "基金";
                    break;
                case TradeConstants.PRODUCT_TYPE_BOND:
                    typeDesc = "债券";
                    break;
                default:
                    typeDesc = "未知类型";
            }
        }
        response.setTypeDesc(typeDesc);
        
        // 设置是否可交易（基于数字状态判断）
        response.setTradable(product.getStatus() != null && product.getStatus().equals(TradeConstants.PRODUCT_STATUS_TRADING));
        
        return response;
    }
}