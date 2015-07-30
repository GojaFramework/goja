/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.plugins.shiro;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;

/**
 * 访客访问控制处理器
 *
 * @author dafei
 */
class GuestAuthzHandler extends AbstractAuthzHandler {
    private static GuestAuthzHandler gah = new GuestAuthzHandler();

    private GuestAuthzHandler() {
    }

    public static GuestAuthzHandler me() {
        return gah;
    }

    @Override
    public void assertAuthorized() throws AuthorizationException {
        if (getSubject().getPrincipal() != null) {
            throw new UnauthenticatedException("Attempting to perform a guest-only operation.  The current Subject is " +
                    "not a guest (they have been authenticated or remembered from a previous login).  Access " +
                    "denied.");
        }
    }

}
