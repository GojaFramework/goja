/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.libs;

import goja.libs.base.Option;

import java.util.Collections;
import java.util.Iterator;

/**
 * <p> . </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-09-11 13:26
 * @since JDK 1.6
 */
public class Some<T> extends Option<T> {
    final T value;

    public Some(T value) {
        this.value = value;
    }

    @Override
    public boolean isDefined() {
        return true;
    }

    @Override
    public T get() {
        return value;
    }

    public Iterator<T> iterator() {
        return Collections.singletonList(value).iterator();
    }

    @Override
    public String toString() {
        return "Some(" + value + ")";
    }
}
