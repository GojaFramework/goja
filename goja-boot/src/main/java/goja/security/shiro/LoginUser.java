package goja.security.shiro;

import com.jfinal.plugin.activerecord.Model;

import java.io.Serializable;

/**
 * <p>登录对象。 </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class LoginUser<U extends Model> implements Serializable {
    /**
     * SERIAL VERSION UID
     */
    private static final long serialVersionUID = 4562084055642717363L;
    /**
     * 应用登录用户
     */
    private final AppUser<U> appUser;
    /**
     * 登录用户密码
     */
    private final String password;

    /**
     * 用户密码盐值
     */
    private final String salt;

    /**
     * 构造函数
     *
     * @param appUser  用户
     * @param password 登录密码
     * @param salt     密码盐值
     */
    public LoginUser(AppUser<U> appUser, String password, String salt) {
        this.appUser = appUser;
        this.password = password;
        this.salt = salt;
    }

    /**
     * 获取登录用户
     *
     * @return 登录用户
     */
    public AppUser<U> getAppUser() {
        return appUser;
    }

    /**
     * 登录密码
     * @return 登录密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 密码盐值
     * @return 密码盐值
     */
    public String getSalt() {
        return salt;
    }
}
