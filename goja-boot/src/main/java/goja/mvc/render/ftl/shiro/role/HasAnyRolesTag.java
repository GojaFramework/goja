/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.mvc.render.ftl.shiro.role;

import goja.core.StringPool;
import org.apache.shiro.subject.Subject;

/**
 * <p>
 * 多个角色的验证.
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-10-27 10:35 AM
 * @since JDK 1.5
 */
public class HasAnyRolesTag extends RoleTag {
    // Delimeter that separates role names in tag attribute

    @Override
    protected boolean showTagBody(String roleName) {
        boolean hasAnyRole = false;
        Subject subject = getSubject();

        if (subject != null) {
            // Iterate through roles and check to see if the user has one of the roles
            for (String role : roleName.split(StringPool.COMMA)) {
                if (subject.hasRole(role.trim())) {
                    hasAnyRole = true;
                    break;
                }
            }
        }

        return hasAnyRole;
    }
}
