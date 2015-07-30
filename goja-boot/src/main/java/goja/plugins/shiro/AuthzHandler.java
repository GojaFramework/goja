/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.plugins.shiro;

import org.apache.shiro.authz.AuthorizationException;

/**
 * 访问控制处理器接口
 *
 * @author dafei
 */
interface AuthzHandler {
    /**
     * 访问控制检查
     *
     * @throws org.apache.shiro.authz.AuthorizationException 授权异常
     */
    public void assertAuthorized() throws AuthorizationException;
}
