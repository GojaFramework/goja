/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.lang.born;

import java.lang.reflect.Method;

import goja.lang.Mirror;

public class DynaMethodBorning<T> implements Borning<T> {

    private Method method;

    public DynaMethodBorning(Method method) {
        this.method = method;
        this.method.setAccessible(true);
    }

    @SuppressWarnings("unchecked")
    public T born(Object... args) {
        try {
            return (T) method.invoke(null, Mirror.evalArgToRealArray(args));
        }
        catch (Exception e) {
            throw new BorningException(e, method.getDeclaringClass(), args);
        }
    }

}
