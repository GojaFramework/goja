/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.security.shiro;

import com.jfinal.plugin.activerecord.Model;

/**
 * <p> 用户信息获取接口. </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-11-01 17:39
 * @since JDK 1.6
 */
public interface SecurityUserData<U extends Model> {

    /**
     * 登录时获取用户的授权信息
     *
     * @param principal 用户信息
     * @return 授权信息
     */
    UserAuth auth(AppUser<U> principal);

    LoginUser<U> user(String loginName);
}
