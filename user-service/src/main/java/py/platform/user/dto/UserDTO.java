package py.platform.user.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 用户信息传输对象（DTO），用于服务间或前后端传递用户数据。
 */
@Data
public class UserDTO implements Serializable {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
} 