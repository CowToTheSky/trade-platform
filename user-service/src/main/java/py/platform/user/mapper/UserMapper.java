package py.platform.user.mapper;

import py.platform.user.model.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问层接口，定义用户相关的数据库操作。
 */
@Mapper
public interface UserMapper {
    /**
     * 根据用户名查询用户信息。
     * @param username 用户名
     * @return 用户实体对象
     */
    User findByUsername(String username);

    /**
     * 新增用户。
     * @param user 用户实体
     * @return 影响行数
     */
    int insert(User user);

    /**
     * 更新用户登录信息。
     * @param user 用户实体
     * @return 影响行数
     */
    int updateLoginInfo(User user);
}
