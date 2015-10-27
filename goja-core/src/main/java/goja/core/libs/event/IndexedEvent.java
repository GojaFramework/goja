/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core.libs.event;

import java.util.concurrent.atomic.AtomicLong;

/**
 * <p> . </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-09-11 13:24
 * @since JDK 1.6
 */
public class IndexedEvent<M> {
    private static final AtomicLong idGenerator = new AtomicLong(1);
    final public M    data;
    final public Long id;

    public IndexedEvent(M data) {
        this.data = data;
        this.id = idGenerator.getAndIncrement();
    }

    public static void resetIdGenerator() {
        idGenerator.set(1);
    }

    @Override
    public String toString() {
        return "Event(id: " + id + ", " + data + ")";
    }
}
