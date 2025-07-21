package py.platform.user.service.impl;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.platform.common.util.IdGenerator;
import com.platform.common.util.JwtUtil;
import com.platform.common.vo.ResponseVO;

import lombok.RequiredArgsConstructor;
import py.platform.user.constant.UserConstants;
import py.platform.user.constant.UserErrorCode;
import py.platform.user.mapper.UserMapper;
import py.platform.user.model.User;
import py.platform.user.service.UserService;
import py.platform.user.vo.LoginRequest;
import py.platform.user.vo.LoginResponse;
import py.platform.user.vo.RegisterRequest;
import py.platform.user.vo.RegisterResponse;

/**
 * 用户中心业务实现类，实现UserService接口定义的用户相关操作。
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    /**日志信息 */
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    /** 用户数据访问对象 */
    private final UserMapper userMapper;

    /** 密码加密器 */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 根据用户名查询用户信息。
     * 
     * @param username 用户名
     * @return 用户实体对象
     */
    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    /**
     * 用户登录
     * 
     * @param request 登录请求参数
     * @return 登录响应（含JWT Token）
     */
    @Override
    public ResponseVO<LoginResponse> login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            logger.info("用户不存在，无法登录");
            return ResponseVO.error(UserErrorCode.USER_NOT_FOUND.getCode(), "该用户不存在!");
        }
        //明文是否与密文匹配
        boolean passwordValid = passwordEncoder.matches(request.getPassword(), user.getPassword());
        // 密码错误
        if (!passwordValid) {
            logger.info("用户{}登录失败，密码错误", request.getPassword());
            return ResponseVO.error(UserErrorCode.PASSWORD_ERROR.getCode(), "密码错误!");
        }
        // 获取token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());
        // 组装响应
        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setUsername(user.getUsername());
        return ResponseVO.success(resp, "登录成功!");
    }

    /**
     * 用户注册
     * 
     * @param request 注册请求参数
     * @return 注册响应
     */
    @Override
    public ResponseVO<RegisterResponse> register(RegisterRequest request) {
        RegisterResponse resp = new RegisterResponse();
        // 参数校验
        if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            return ResponseVO.error(UserErrorCode.USER_NAME_PASSWORD_EMPTY.getCode(), "用户名和密码不能为空!");
        }
        // 唯一性校验
        if (userMapper.findByUsername(request.getUsername()) != null) {
            return ResponseVO.error(UserErrorCode.USERNAME_EXISTS.getCode(), "用户名已存在!");
        }
        // 密码加密
        String encodedPwd = passwordEncoder.encode(request.getPassword());
        // 构造User对象
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
        // 入库
        userMapper.insert(user);
        // 组装响应
        resp.setUserId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setEmail(user.getEmail());
        resp.setPhone(user.getPhone());
        resp.setRole(user.getRole());
        resp.setAvatarUrl(user.getAvatarUrl());
        resp.setRegisterTime(user.getCreatedAt());
        resp.setFirstLogin(true);
        return ResponseVO.success(resp);
    }
}