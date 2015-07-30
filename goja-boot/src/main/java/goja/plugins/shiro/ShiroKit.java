/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.plugins.shiro;

import java.util.concurrent.ConcurrentMap;


/**
 * ShiroKit. (Singleton, ThreadSafe)
 *
 * @author dafei
 */
public class ShiroKit {

    /**
     * 用来记录那个action或者actionpath中是否有shiro认证注解。
     */
    private static ConcurrentMap<String, AuthzHandler> authzMaps = null;

    /**
     * 禁止初始化
     */
    private ShiroKit() {
    }

    static void init(ConcurrentMap<String, AuthzHandler> maps) {
        authzMaps = maps;
    }

    static AuthzHandler getAuthzHandler(String actionKey) {
        /*
        if(authzMaps.containsKey(controllerClassName)){
			return true;
		}*/
        return authzMaps.get(actionKey);
    }
}
