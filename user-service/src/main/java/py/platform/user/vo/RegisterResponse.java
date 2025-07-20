package py.platform.user.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户注册响应参数（企业级）
 */
@Data
public class RegisterResponse {
    /** 用户ID */
    private Long userId;
    /** 用户名 */
    private String username;
    /** 邮箱 */
    private String email;
    /** 手机号 */
    private String phone;
    /** 用户角色 */
    private String role;
    /** 头像URL */
    private String avatarUrl;
    /** 注册时间 */
    private LocalDateTime registerTime;
    /** JWT Token（如注册即登录） */
    private String token;
    /** 是否首次登录 */
    private Boolean firstLogin;
    /** 企业ID（如有） */
    private Long companyId;
    /** 企业名称（如有） */
    private String companyName;
    // 可扩展更多字段
} 