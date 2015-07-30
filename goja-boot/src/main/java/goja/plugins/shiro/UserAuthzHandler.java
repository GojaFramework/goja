/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.plugins.shiro;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;

/**
 * 认证通过或已记住的用户访问控制处理器
 * 单例模式运行。
 *
 * @author dafei
 */
class UserAuthzHandler extends AbstractAuthzHandler {
    private static UserAuthzHandler uah = new UserAuthzHandler();

    private UserAuthzHandler() {
    }

    public static UserAuthzHandler me() {
        return uah;
    }

    @Override
    public void assertAuthorized() throws AuthorizationException {
        if (getSubject().getPrincipal() == null) {
            throw new UnauthenticatedException("Attempting to perform a user-only operation.  The current Subject is " +
                    "not a user (they haven't been authenticated or remembered from a previous login).  " +
                    "Access denied.");
        }
    }
}
