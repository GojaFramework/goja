/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.mvc.render.ftl.shiro;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import goja.mvc.render.ftl.shiro.auth.*;
import goja.mvc.render.ftl.shiro.permission.HasAnyPermissionsTag;
import goja.mvc.render.ftl.shiro.permission.HasPermissionTag;
import goja.mvc.render.ftl.shiro.permission.LacksPermissionTag;
import goja.mvc.render.ftl.shiro.role.HasAnyRolesTag;
import goja.mvc.render.ftl.shiro.role.HasRoleTag;
import goja.mvc.render.ftl.shiro.role.LacksRoleTag;

/**
 * <p>
 * Apache Shirio authentication Freemarker instructions.
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-10-27 10:42 AM
 * @since JDK 1.5
 */
public class ShiroTags extends SimpleHash {

    private static final long serialVersionUID = -7857361083450860303L;

    /**
     * Constructs an empty hash that uses the default wrapper set in
     */
    public ShiroTags(ObjectWrapper wrapper) {
        super(wrapper);
        put("authenticated", new AuthenticatedTag());
        put("guest", new GuestTag());
        put("hasAnyRoles", new HasAnyRolesTag());
        put("hasPermission", new HasPermissionTag());
        put("hasAnyPermissions", new HasAnyPermissionsTag());
        put("hasRole", new HasRoleTag());
        put("lacksPermission", new LacksPermissionTag());
        put("lacksRole", new LacksRoleTag());
        put("notAuthenticated", new NotAuthenticatedTag());
        put("principal", new PrincipalTag());
        put("user", new UserTag());
    }
}
