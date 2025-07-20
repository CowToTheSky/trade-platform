package py.platform.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import py.platform.user.constant.UserConstants;
import py.platform.user.mapper.UserMapper;
import py.platform.user.model.User;
import py.platform.user.service.UserService;
import py.platform.user.vo.LoginRequest;
import py.platform.user.vo.LoginResponse;
import py.platform.user.vo.RegisterRequest;
import py.platform.user.vo.RegisterResponse;
import com.platform.common.util.IdGenerator;

import java.time.LocalDateTime;

/**
 * 用户中心业务实现类，实现UserService接口定义的用户相关操作。
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    /** 用户数据访问对象 */
    private final UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 根据用户名查询用户信息。
     * @param username 用户名
     * @return 用户实体对象
     */
    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    /**
     * 用户登录操作。
     * @param request 登录请求参数
     * @return 登录响应（含JWT Token）
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        return null;
    }

    /**
     * 用户注册（企业级实现）。
     * @param request 注册请求参数
     * @return 注册响应
     */
    @Override
    public RegisterResponse register(RegisterRequest request) {
        // 1. 参数校验
        if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            throw new IllegalArgumentException("用户名和密码不能为空");
        }
        // 2. 唯一性校验
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        // 3. 密码加密
        String encodedPwd = passwordEncoder.encode(request.getPassword());
        // 4. 构造User对象
        User user = new User();
        // 生成用户ID
        user.setId(IdGenerator.generateUserId());
        user.setUsername(request.getUsername());
        user.setPassword(encodedPwd);
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        // 正常
        user.setStatus(UserConstants.USER_STATUS_ENABLED);
        user.setRole(UserConstants.ROLE_USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        // 5. 入库
        userMapper.insert(user);
        // 6. 组装响应
        RegisterResponse resp = new RegisterResponse();
        resp.setUserId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setEmail(user.getEmail());
        resp.setPhone(user.getPhone());
        resp.setRole(user.getRole());
        resp.setAvatarUrl(user.getAvatarUrl());
        resp.setRegisterTime(user.getCreatedAt());
        resp.setFirstLogin(true);
        // 企业相关字段可根据业务补充
        return resp;
    }
} 