/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.plugins.shiro;

import org.apache.shiro.authz.AuthorizationException;

import java.util.List;

/**
 * 组合模式访问控制处理器
 *
 * @author dafei
 */
class CompositeAuthzHandler implements AuthzHandler {

    private final List<AuthzHandler> authzHandlers;

    public CompositeAuthzHandler(List<AuthzHandler> authzHandlers) {
        this.authzHandlers = authzHandlers;
    }

    @Override
    public void assertAuthorized() throws AuthorizationException {
        for (AuthzHandler authzHandler : authzHandlers) {
            authzHandler.assertAuthorized();
        }
    }
}
