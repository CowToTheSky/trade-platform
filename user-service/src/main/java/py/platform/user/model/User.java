package py.platform.user.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类，表示系统中的用户信息。
 * <p>使用Lombok @Data自动生成getter/setter、toString、equals、hashCode等方法。</p>
 */
@Data
public class User {
    /**
     * 用户ID
     */
    private Long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码（加密存储）
     */
    private String password;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 用户状态（如：0-禁用，1-正常）
     */
    private Integer status;
    /**
     * 用户角色（如：admin、user等）
     */
    private String role;
    /**
     * 头像URL
     */
    private String avatarUrl;
    /**
     * 上次登录IP
     */
    private String lastLoginIp;
    /**
     * 上次登录时间
     */
    private LocalDateTime lastLoginTime;
    /**
     * 连续登录失败次数
     */
    private Integer loginFailCount;
    /**
     * 账户锁定截止时间
     */
    private LocalDateTime lockedUntil;
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
