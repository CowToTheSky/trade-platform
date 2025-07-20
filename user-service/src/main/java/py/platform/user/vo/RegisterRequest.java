package py.platform.user.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 用户注册请求参数
 * 用于封装用户注册时提交的信息。
 * 使用Lombok @Data自动生成getter/setter等方法。
 */
@Data
@Validated
public class RegisterRequest {
    /**
     * 用户名，不能为空，长度限制为4-20个字符
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20个字符之间")
    private String username;

    /** 密码 */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;
} 