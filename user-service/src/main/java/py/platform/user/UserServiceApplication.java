package py.platform.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用户服务应用启动类。
 */
@SpringBootApplication
@MapperScan("py.platform.user.mapper")
public class UserServiceApplication {
    /**
     * 应用程序入口方法。
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}