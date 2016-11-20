package goja.security.shiro;

import java.util.List;

/**
 * <p>用户授权信息 </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class UserAuth {

    /**
     * 用户角色
     */
    private final List<String> roles;
    /**
     * 用户权限
     */
    private final List<String> permissions;

    /**
     * 构造函数
     *
     * @param roles       用户的角色
     * @param permissions 用户的权限
     */
    public UserAuth(List<String> roles, List<String> permissions) {
        this.roles = roles;
        this.permissions = permissions;
    }

    /**
     * 用户的角色
     *
     * @return 用户的角色
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * 用户的权限
     *
     * @return 用户的权限
     */
    public List<String> getPermissions() {
        return permissions;
    }
}
