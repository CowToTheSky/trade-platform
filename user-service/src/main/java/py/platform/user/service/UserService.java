package py.platform.user.service;

import com.platform.common.vo.ResponseVO;

import py.platform.user.model.User;
import py.platform.user.vo.LoginRequest;
import py.platform.user.vo.LoginResponse;
import py.platform.user.vo.RegisterRequest;
import py.platform.user.vo.RegisterResponse;
/**
 * 用户中心业务接口，定义用户相关的核心操作。
 */
public interface UserService {
    /**
     * 根据用户名查询用户信息。
     * @param username 用户名
     * @return 用户实体对象
     */
    User findByUsername(String username);

    /**
     * 用户登录操作。
     * @param request 登录请求参数
     * @return 登录响应（含JWT Token）
     */
    ResponseVO<LoginResponse> login(LoginRequest request);

    /**
     * 用户注册（示例，需实现）。
     */
    ResponseVO<RegisterResponse> register(RegisterRequest request);

        /**
     * 获取用户信息（示例，需实现）。
     */
    // User getUserInfo(String token);
}