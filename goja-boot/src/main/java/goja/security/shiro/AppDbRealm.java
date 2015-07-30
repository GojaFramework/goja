/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.security.shiro;

import goja.Goja;
import goja.encry.EncodeKit;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

/**
 * <p>
 * 用户授权信息.
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-11-01 17:37
 * @since JDK 1.6
 */
public class AppDbRealm extends AuthorizingRealm {


    public AppDbRealm() {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(EncodeKit.HASH_ALGORITHM);
        matcher.setHashIterations(EncodeKit.HASH_INTERATIONS);

        setCredentialsMatcher(matcher);

    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        final SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        final UserAuth auth = Goja.securityUserData.auth((AppUser) principals.getPrimaryPrincipal());
        info.addRoles(auth.getRoles());
        info.addStringPermissions(auth.getPermissions());
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        LoginUser loginUser = Goja.securityUserData.user(token.getUsername());
        if (loginUser == null) {
            return null;
        }
        final AppUser appUser = loginUser.getAppUser();
        if (appUser == null) {
            return null;
        }
        byte[] salt = EncodeKit.decodeHex(loginUser.getSalt());
        return new SimpleAuthenticationInfo(appUser, loginUser.getPassword()
                , ByteSource.Util.bytes(salt), getName());
    }
}
