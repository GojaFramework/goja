/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.plugins.shiro;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;

import java.lang.annotation.Annotation;

/**
 * 基于权限的访问控制处理器，非单例模式运行。
 *
 * @author dafei
 */
class PermissionAuthzHandler extends AbstractAuthzHandler {
    private final Annotation annotation;

    public PermissionAuthzHandler(Annotation annotation) {
        this.annotation = annotation;
    }

    @Override
    public void assertAuthorized() throws AuthorizationException {
        if (!(annotation instanceof RequiresPermissions))
            return;

        RequiresPermissions rpAnnotation = (RequiresPermissions) annotation;
        String[] perms = rpAnnotation.value();
        Subject subject = getSubject();

        if (perms.length == 1) {
            subject.checkPermission(perms[0]);
            return;
        }
        if (Logical.AND.equals(rpAnnotation.logical())) {
            getSubject().checkPermissions(perms);
            return;
        }
        if (Logical.OR.equals(rpAnnotation.logical())) {
            // Avoid processing exceptions unnecessarily - "delay" throwing the
            // exception by calling hasRole first
            boolean hasAtLeastOnePermission = false;
            for (String permission : perms)
                if (getSubject().isPermitted(permission))
                    hasAtLeastOnePermission = true;
            // Cause the exception if none of the role match, note that the
            // exception message will be a bit misleading
            if (!hasAtLeastOnePermission)
                getSubject().checkPermission(perms[0]);

        }

    }

}
