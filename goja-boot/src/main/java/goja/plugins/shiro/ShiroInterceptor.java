/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.plugins.shiro;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;

public class ShiroInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation ai) {
        AuthzHandler ah = ShiroKit.getAuthzHandler(ai.getActionKey());
        // 存在访问控制处理器。
        if (ah != null) {
            try {
                // 执行权限检查。
                ah.assertAuthorized();
            } catch (UnauthenticatedException lae) {
                // RequiresGuest，RequiresAuthentication，RequiresUser，未满足时，抛出未经授权的异常。
                // 如果没有进行身份验证，返回HTTP401状态码
                ai.getController().renderError(401);
                return;
            } catch (AuthorizationException ae) {
                // RequiresRoles，RequiresPermissions授权异常
                // 如果没有权限访问对应的资源，返回HTTP状态码403。
                ai.getController().renderError(403);
                return;
            } catch (Exception e) {
                // 出现了异常，应该是没有登录。
                ai.getController().renderError(401);
                return;
            }
        }
        // 执行正常逻辑
        ai.invoke();
    }
}
