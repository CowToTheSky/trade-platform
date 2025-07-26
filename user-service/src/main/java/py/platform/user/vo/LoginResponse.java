package py.platform.user.vo;

import lombok.Data;

/**
 * 用户登录响应参数
 * <p>用于封装登录成功后返回给前端的信息。</p>
 * <p>使用Lombok @Data自动生成getter/setter等方法。</p>
 */
@Data
public class LoginResponse {
    /** 用户ID */
    private Long id;
    /** JWT Token */
    private String token;
    /** 用户名 */
    private String username;
    // 可扩展更多用户信息
}