/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.lang.eject;

import goja.lang.Lang;

import java.lang.reflect.Method;

public class EjectByGetter implements Ejecting {


    private Method getter;

    public EjectByGetter(Method getter) {
        this.getter = getter;
    }

    public Object eject(Object obj) {
        try {
            return null == obj ? null : getter.invoke(obj);
        } catch (Exception e) {
            throw Lang.makeThrow("Fail to invoke getter %s.'%s()' because [%s]: %s",
                    getter.getDeclaringClass().getName(),
                    getter.getName(),
                    Lang.unwrapThrow(e),
                    Lang.unwrapThrow(e).getMessage());
        }
    }

}
