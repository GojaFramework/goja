/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.plugins.shiro;

import goja.core.app.GojaConfig;
import goja.mvc.AjaxSimple;
import goja.rapid.mvc.kits.Requests;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.render.JsonRender;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;

import javax.servlet.http.HttpServletRequest;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public class ShiroInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation ai) {
        AuthzHandler ah = ShiroKit.getAuthzHandler(ai.getActionKey());
        // 存在访问控制处理器。
        if (ah != null) {

            final Controller controller = ai.getController();

            try {
                // 执行权限检查。
                ah.assertAuthorized();
                // 执行正常逻辑
                ai.invoke();
            } catch (UnauthenticatedException lae) {
                invokeUnauthorized(controller);

            } catch (AuthorizationException ae) {
                // RequiresRoles，RequiresPermissions授权异常
                // 如果没有权限访问对应的资源，返回HTTP状态码403。
                if (GojaConfig.isApi()) {
                    AjaxSimple ajaxSimple = AjaxSimple.Builder().setCode(String.valueOf(SC_FORBIDDEN)).setSuccess(false).create();
                    controller.renderError(SC_FORBIDDEN, new JsonRender(ajaxSimple));
                    return;
                }
                final HttpServletRequest request = controller.getRequest();
                final boolean isAjax = Requests.ajax(request);
                if (isAjax) {
                    AjaxSimple ajaxSimple = AjaxSimple.Builder().setCode(String.valueOf(SC_FORBIDDEN)).setSuccess(false).create();
                    controller.renderError(SC_FORBIDDEN, new JsonRender(ajaxSimple));
                    return;
                }
                controller.renderError(SC_FORBIDDEN);
            } catch (Exception e) {
                // 出现了异常，应该是没有登录。
                invokeUnauthorized(controller);
            }
        } else {
            // 执行正常逻辑
            ai.invoke();
        }
    }

    private boolean invokeUnauthorized(Controller controller) {
        // RequiresGuest，RequiresAuthentication，RequiresUser，未满足时，抛出未经授权的异常。
        // 如果没有进行身份验证，返回HTTP401状态码
        if (GojaConfig.isApi()) {
            AjaxSimple ajaxSimple = AjaxSimple.Builder().setCode(String.valueOf(SC_UNAUTHORIZED)).setSuccess(false).create();
            controller.renderError(SC_UNAUTHORIZED, new JsonRender(ajaxSimple));
            return true;
        }
        final HttpServletRequest request = controller.getRequest();
        final boolean isAjax = Requests.ajax(request);
        if (isAjax) {
            AjaxSimple ajaxSimple = AjaxSimple.Builder().setCode(String.valueOf(SC_UNAUTHORIZED)).setSuccess(false).create();
            controller.renderError(SC_UNAUTHORIZED, new JsonRender(ajaxSimple));
            return true;
        }
        controller.renderError(SC_UNAUTHORIZED);
        return false;
    }
}
