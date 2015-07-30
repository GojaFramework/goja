package goja.security.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

/**
 * <p> 执行登录处理的协助类 </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public abstract class LoginActionKit {

    /**
     * 触发Shiro的登录请求.
     *
     * @param username   用户名称
     * @param password   登录密码
     * @param rememberMe 是否记住我
     * @throws AuthenticationException 授权失败的异常
     */
    public static void login(String username, String password, boolean rememberMe) throws AuthenticationException {
        UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
        final Subject subject = SecurityUtils.getSubject();
        subject.login(token);
    }

    /**
     * 触发Shiro的登录请求.默认的不记住我.
     *
     * @param username 用户名称
     * @param password 登录密码
     * @throws AuthenticationException 授权失败的异常
     */
    public static void login(String username, String password) throws AuthenticationException {
        login(username, password, false);
    }


}
