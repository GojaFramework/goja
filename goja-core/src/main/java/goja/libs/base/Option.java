/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.libs.base;

import goja.libs.Some;

/**
 * <p> . </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-09-11 13:25
 * @since JDK 1.6
 */
public abstract class Option<T> implements Iterable<T> {

    private static None<Object> None = new None<Object>();

    public static <T> None<T> None() {
        return (goja.libs.base.None<T>) None;
    }

    public static <T> Some<T> Some(T value) {
        return new Some<T>(value);
    }

    public abstract boolean isDefined();

    public abstract T get();
}
