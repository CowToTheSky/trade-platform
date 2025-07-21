package py.platform.user.constant;

/**
 * 用户相关错误码定义
 */
public enum UserErrorCode {
    SUCCESS(200, "操作成功"),
    PARAM_MISSING(100001, "参数缺失"),
    USERNAME_EXISTS(100002, "用户名已存在"),
    PASSWORD_ERROR(100003, "密码错误"),
    USER_NOT_FOUND(100004, "用户不存在"),
    USER_NAME_PASSWORD_EMPTY(100005, "用户名和密码不能为空"),
    UNKNOWN_ERROR(100099, "服务器内部错误");

    private final int code;
    private final String message;

    UserErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
} 