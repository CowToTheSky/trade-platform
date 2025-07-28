package com.platform.trade.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.platform.common.vo.ResponseVO;
import com.platform.trade.service.ProductService;
import com.platform.trade.vo.ProductResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;

/**
 * 产品控制器
 * 
 * @author Trade Platform Team
 */
@Slf4j
@RestController
@RequestMapping("/api/product")
@Tag(name = "产品管理", description = "产品信息相关接口")
@Validated
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * 根据产品代码查询产品信息
     */
    @GetMapping("/{code}")
    @Operation(summary = "查询产品信息", description = "根据产品代码查询产品详细信息")
    public ResponseVO<ProductResponse> getProductByCode(
            @Parameter(description = "产品代码") @PathVariable @NotBlank String code) {
        try {
            log.info("查询产品信息: code={}", code);
            ProductResponse product = productService.getProductByCode(code);
            if (product == null) {
                return ResponseVO.error(404, "产品不存在");
            }
            return ResponseVO.success(product);
        } catch (Exception e) {
            log.error("查询产品信息失败: {}", e.getMessage(), e);
            return ResponseVO.error(500, e.getMessage());
        }
    }
    
    /**
     * 查询可交易产品列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询可交易产品列表", description = "分页查询所有可交易的产品列表")
    public ResponseVO<PageInfo<ProductResponse>> getTradableProducts(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") @Min(1) int size) {
        try {
            log.info("查询可交易产品列表: page={}, size={}", page, size);
            
            PageInfo<ProductResponse> pageInfo = productService.getTradableProducts(page, size);
            
            return ResponseVO.success(pageInfo);
        } catch (Exception e) {
            log.error("查询可交易产品列表失败: {}", e.getMessage(), e);
            return ResponseVO.error(500, e.getMessage());
        }
    }
    
    /**
     * 根据产品类型查询产品列表
     */
    @GetMapping("/type/{type}")
    @Operation(summary = "按类型查询产品", description = "根据产品类型分页查询产品列表")
    public ResponseVO<PageInfo<ProductResponse>> getProductsByType(
            @Parameter(description = "产品类型：1-股票，2-基金，3-债券") @PathVariable @Min(1) Integer type,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") @Min(1) int size) {
        try {
            log.info("按类型查询产品: type={}, page={}, size={}", type, page, size);
            
            PageInfo<ProductResponse> pageInfo = productService.getProductsByType(type, page, size);
            
            return ResponseVO.success(pageInfo);
        } catch (Exception e) {
            log.error("按类型查询产品失败: {}", e.getMessage(), e);
            return ResponseVO.error(500, e.getMessage());
        }
    }
    
    /**
     * 根据产品名称搜索产品
     */
    @GetMapping("/search")
    @Operation(summary = "搜索产品", description = "根据产品名称关键字搜索产品")
    public ResponseVO<PageInfo<ProductResponse>> searchProducts(
            @Parameter(description = "产品名称关键字") @RequestParam @NotBlank String name,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") @Min(1) int size) {
        try {
            log.info("搜索产品: name={}, page={}, size={}", name, page, size);
            
            PageInfo<ProductResponse> pageInfo = productService.searchProductsByName(name, page, size);
            
            return ResponseVO.success(pageInfo);
        } catch (Exception e) {
            log.error("搜索产品失败: {}", e.getMessage(), e);
            return ResponseVO.error(500, e.getMessage());
        }
    }
    
    /**
     * 检查产品是否可交易
     */
    @GetMapping("/{code}/tradable")
    @Operation(summary = "检查产品可交易性", description = "检查指定产品是否可以交易")
    public ResponseVO<Map<String, Object>> checkTradable(
            @Parameter(description = "产品代码") @PathVariable @NotBlank String code) {
        try {
            log.info("检查产品可交易性: code={}", code);
            
            boolean tradable = productService.isProductTradable(code);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", code);
            result.put("tradable", tradable);
            result.put("message", tradable ? "可以交易" : "不可交易");
            
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("检查产品可交易性失败: {}", e.getMessage(), e);
            return ResponseVO.error(500, e.getMessage());
        }
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查产品服务是否正常运行")
    public ResponseVO<String> health() {
        return ResponseVO.success("产品服务运行正常");
    }
}