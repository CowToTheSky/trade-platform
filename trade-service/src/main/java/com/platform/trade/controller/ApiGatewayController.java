package com.platform.trade.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * API网关控制器
 * 根据不同的接口路径将请求转发到相应的微服务
 * - 用户相关API：转发到user-service (8081端口)
 * - 交易相关API：在trade-service本地处理
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiGatewayController {

    @Value("${user.service.url:http://localhost:8081}")
    private String userServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    // ==================== 用户服务相关API转发 ====================
    
    /**
     * 用户注册 - 转发到user-service
     */
    @PostMapping("/user/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        return forwardToUserService("/api/user/register", HttpMethod.POST, requestBody, request);
    }

    /**
     * 用户登录 - 转发到user-service
     */
    @PostMapping("/user/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        return forwardToUserService("/api/user/login", HttpMethod.POST, requestBody, request);
    }

    /**
     * 获取用户信息 - 转发到user-service
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long userId, HttpServletRequest request) {
        return forwardToUserService("/api/user/" + userId, HttpMethod.GET, null, request);
    }

    /**
     * 更新用户信息 - 转发到user-service
     */
    @PutMapping("/user/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        return forwardToUserService("/api/user/" + userId, HttpMethod.PUT, requestBody, request);
    }

    /**
     * 用户列表 - 转发到user-service
     */
    @GetMapping("/user/list")
    public ResponseEntity<?> getUserList(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        HttpServletRequest request) {
        String queryParams = "?page=" + page + "&size=" + size;
        return forwardToUserService("/api/user/list" + queryParams, HttpMethod.GET, null, request);
    }

    // ==================== 交易服务相关API (本地处理，无需转发) ====================
    // 注意：交易相关的API已经在ProductController和OrderController中实现
    // 这里不需要额外的转发逻辑，因为请求直接到达trade-service
    
    /**
     * 转发请求到user-service
     */
    private ResponseEntity<?> forwardToUserService(String path, HttpMethod method, Object requestBody, HttpServletRequest request) {
        try {
            String url = userServiceUrl + path;
            
            // 复制请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            
            // 如果有Authorization头，也复制过去
            String authorization = request.getHeader("Authorization");
            if (authorization != null) {
                headers.set("Authorization", authorization);
            }

            HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);
            
            return restTemplate.exchange(url, method, entity, Object.class);
        } catch (Exception e) {
            // 如果转发失败，返回错误信息
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "用户服务暂时不可用，请稍后重试",
                "error", e.getMessage()
            ));
        }
    }
}