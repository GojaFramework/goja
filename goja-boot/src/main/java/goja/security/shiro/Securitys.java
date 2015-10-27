/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.security.shiro;


import com.jfinal.plugin.activerecord.Model;
import org.apache.shiro.subject.Subject;

import static org.apache.shiro.SecurityUtils.getSubject;

/**
 * <p>
 * .
 * </p>
 *
 * @author walter yang
 * @version 1.0 2013-10-29 11:05 PM
 * @since JDK 1.5
 */
public final class Securitys {
    /**
     * Get login user.
     *
     * @return the login user.
     */
    public static <U extends Model> AppUser<U> getLogin() {

        return (AppUser< U>) getSubject().getPrincipal();
    }

    /**
     * the logout system.
     */
    public static void logout() {
        if (isLogin()) {
            getSubject().logout();
        }

    }

    /**
     * Is login.
     *
     * @return the boolean
     */
    public static boolean isLogin() {
        Subject subject = getSubject();

        return subject.getPrincipal() != null;
    }

    /**
     * Whether the logged and through authentication
     *
     * @return true logged and auth.
     */
    public static boolean isLoginAndAuth() {

        Subject subject = getSubject();

        return subject.getPrincipal() != null && subject.isAuthenticated();
    }

    /**
     * Is permitted.
     *
     * @param permission the permission
     * @return the boolean
     */
    public static boolean isPermitted(String permission) {
        return getSubject().isPermitted(permission);
    }

    /**
     * Is role.
     *
     * @param role the role
     * @return the boolean
     */
    public static boolean isRole(String role) {
        return getSubject().hasRole(role);
    }
}
