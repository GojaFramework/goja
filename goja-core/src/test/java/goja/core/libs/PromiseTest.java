/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core.libs;

import org.junit.Test;

import static org.junit.Assert.*;

public class PromiseTest {

    @Test
    public void testNow() throws Exception {
        Promise<Integer> promise = new Promise<Integer>();
        promise.invoke(100);
        assertTrue(promise.isDone());

    }
}