package py.platform.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import py.platform.user.model.User;
import py.platform.user.service.UserService;
import py.platform.user.vo.LoginRequest;
import py.platform.user.vo.LoginResponse;
import com.platform.common.vo.ResponseVO;
import py.platform.user.vo.RegisterRequest;
import py.platform.user.vo.RegisterResponse;

/**
 * 用户中心控制器，负责处理用户注册、登录、信息等相关接口请求。
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    /** 用户业务服务 */
    private final UserService userService;

    /**
     * 用户登录接口
     * @param request 登录请求参数
     * @return 登录响应（含JWT Token）
     */
    @PostMapping("/login")
    public ResponseVO<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse resp = userService.login(request);
        return ResponseVO.success(resp);
    }

    /**
     * 用户注册接口（示例，需实现）
     * @return 操作结果
     */
    @PostMapping("/register")
    public ResponseVO<RegisterResponse> register(@RequestBody @Validated
                                                 RegisterRequest request) {
        RegisterResponse resp = userService.register(request);
        return ResponseVO.success(resp);
    }

    /**
     * 获取当前用户信息接口（示例，需实现）
     * @return 用户信息
     */
    @GetMapping("/info")
    public ResponseVO<User> getUserInfo(/* @RequestHeader("Authorization") String token */) {
        // TODO: 实现获取用户信息逻辑
        return ResponseVO.success(null);
    }

    /**
     * 测试接口 - 验证应用程序是否正常运行
     */
    @GetMapping("/test")
    public ResponseVO<String> test() {
        return ResponseVO.success("应用程序运行正常！");
    }

    /**
     * 测试MyBatis配置接口
     */
    @GetMapping("/test-mapper")
    public ResponseVO<String> testMapper() {
        try {
            // 这里只是测试MyBatis配置，不实际调用数据库
            return ResponseVO.success("MyBatis配置正常！");
        } catch (Exception e) {
            return ResponseVO.error(500, "MyBatis配置异常: " + e.getMessage());
        }
    }
} 