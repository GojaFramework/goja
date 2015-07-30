/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.lang.born;

import java.lang.reflect.Method;

import goja.lang.Lang;

public class MethodCastingBorning<T> implements Borning<T> {

    private Method method;
    private Class<?>[] pts;

    public MethodCastingBorning(Method method) {
        this.method = method;
        this.method.setAccessible(true);
        this.pts = method.getParameterTypes();
    }

    @SuppressWarnings("unchecked")
    public T born(Object... args) {
        try {
            args = Lang.array2ObjectArray(args, pts);
            return (T) method.invoke(null, args);
        }
        catch (Exception e) {
            throw new BorningException(e, method.getDeclaringClass(), args);
        }
    }
}
