/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.lang.inject;

import goja.castor.Castors;
import goja.lang.Lang;

import java.lang.reflect.Method;

public class InjectBySetter implements Injecting {


    private Method   setter;
    private Class<?> valueType;

    public InjectBySetter(Method setter) {
        this.setter = setter;
        valueType = setter.getParameterTypes()[0];
    }

    public void inject(Object obj, Object value) {
        Object v = null;
        try {
            v = Castors.me().castTo(value, valueType);
            setter.invoke(obj, v);
        } catch (Exception e) {
            throw Lang.makeThrow("Fail to set '%s'[ %s ] by setter %s.'%s()' because [%s]: %s",
                    value,
                    v,
                    setter.getDeclaringClass().getName(),
                    setter.getName(),
                    Lang.unwrapThrow(e),
                    Lang.unwrapThrow(e).getMessage());
        }
    }

}