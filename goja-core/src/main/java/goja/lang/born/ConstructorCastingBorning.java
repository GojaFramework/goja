/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.lang.born;

import java.lang.reflect.Constructor;

import goja.lang.Lang;

public class ConstructorCastingBorning<T> implements Borning<T> {

    private Constructor<T> c;
    private Class<?>[] pts;

    public ConstructorCastingBorning(Constructor<T> c) {
        this.c = c;
        this.c.setAccessible(true);
        this.pts = c.getParameterTypes();
    }

    public T born(Object... args) {
        try {
            args = Lang.array2ObjectArray(args, pts);
            return c.newInstance(args);
        }
        catch (Exception e) {
            throw new BorningException(e, c.getDeclaringClass(), args);
        }
    }

}
