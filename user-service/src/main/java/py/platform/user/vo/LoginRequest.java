package py.platform.user.vo;

import lombok.Data;

/**
 * 用户登录请求参数
 * <p>用于封装用户登录时提交的信息。</p>
 * <p>使用Lombok @Data自动生成getter/setter等方法。</p>
 */
@Data
public class LoginRequest {
    /** 用户名 */
    private String username;
    /** 密码 */
    private String password;
} 