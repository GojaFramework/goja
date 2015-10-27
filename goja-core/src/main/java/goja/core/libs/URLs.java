/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core.libs;

import goja.core.StringPool;
import goja.core.exceptions.UnexpectedException;

import java.net.URLEncoder;

/**
 * <p>
 * .
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-04-04 10:28
 * @since JDK 1.6
 */
public class URLs {

    public static String addParam(String originalUrl, String name, String value) {
        return originalUrl + (originalUrl.contains("?") ? "&" : "?") + encodePart(name) + "=" + encodePart(value);
    }

    public static String encodePart(String part) {
        try {
            return URLEncoder.encode(part, StringPool.UTF_8);
        } catch (Exception e) {
            throw new UnexpectedException(e);
        }
    }
}
