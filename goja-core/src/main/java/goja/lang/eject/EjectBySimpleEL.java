/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.lang.eject;

import goja.kits.base.Strs;
import goja.lang.Lang;

import java.lang.reflect.Method;

public class EjectBySimpleEL implements Ejecting {

    private String by;

    private Method method;

    public EjectBySimpleEL(String by) {
        if (Strs.isBlank(by))
            throw new IllegalArgumentException("MUST NOT Null/Blank");
        if (by.indexOf('#') > 0) {
            try {
                method = Class.forName(by.substring(0, by.indexOf('#')))
                                .getMethod(by.substring(by.indexOf('#')+1), Object.class);
            }
            catch (Throwable e) {
                throw Lang.wrapThrow(e);
            }
        }
        this.by = by;
    }

    public Object eject(Object obj) {
        try {
            if (method != null)
                return method.invoke(null, obj);
            if (obj == null)
                return null;
            return obj.getClass().getMethod(by).invoke(obj);
        }
        catch (Throwable e) {
            throw Lang.wrapThrow(e);
        }
    }

}
