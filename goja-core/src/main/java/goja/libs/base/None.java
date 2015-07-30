/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.libs.base;

import java.util.Collections;
import java.util.Iterator;

/**
 * <p> . </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-09-11 13:26
 * @since JDK 1.6
 */
 class None<T> extends Option<T>{

    @Override
    public boolean isDefined() {
        return false;
    }

    @Override
    public T get() {
        throw new IllegalStateException("No value");
    }

    public Iterator<T> iterator() {
        return Collections.<T>emptyList().iterator();
    }

    @Override
    public String toString() {
        return "None";
    }
}
