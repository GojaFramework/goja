/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core.libs;

import org.junit.Test;

/**
 * <p> . </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-04-04 10:43
 * @since JDK 1.6
 */
public class MimeTypesTest {
    @Test
    public void testMime() throws Exception {

        final String contentType = MimeTypes.getContentType("abc.png");
        System.out.println(contentType);
    }
}
