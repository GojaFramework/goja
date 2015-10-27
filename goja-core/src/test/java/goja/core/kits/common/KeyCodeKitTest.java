/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core.kits.common;

import goja.core.kits.base.KeyCodeKit;
import org.junit.Test;

/**
 * <p>
 * .
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-02-07 12:38
 * @since JDK 1.6
 */
public class KeyCodeKitTest {
    @Test
    public void testGenerateKeyCode() throws Exception {
        final String key1 = KeyCodeKit.generateKeyCode("11111", "jfina-app");
        final String key2 = KeyCodeKit.generateKeyCode("11111", "jfina-app");
        System.out.println(key1 + " " + key2);
    }
}
