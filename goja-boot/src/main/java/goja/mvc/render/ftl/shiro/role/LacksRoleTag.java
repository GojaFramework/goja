/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.mvc.render.ftl.shiro.role;

import org.apache.shiro.subject.Subject;

/**
 * <p>
 * 判定当前登录人是否有某个角色信息.如果没有返回true
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-10-27 10:40 AM
 * @since JDK 1.5
 */
public class LacksRoleTag extends RoleTag {
    @Override
    protected boolean showTagBody(String roleName) {
        final Subject subject = getSubject();
        return !(subject != null && subject.hasRole(roleName));
    }
}
