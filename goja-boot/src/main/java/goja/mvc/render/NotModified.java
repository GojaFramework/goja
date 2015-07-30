/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.mvc.render;

import com.google.common.base.Strings;
import com.jfinal.render.Render;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 304 Not Modified.
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-03-29 14:06
 * @since JDK 1.6
 */
public class NotModified extends Render {

    String etag;

    public NotModified() {
        this("NotModified");
    }

    public NotModified(String etag) {
        this.etag = etag;
    }

    @Override
    public void render() {
        response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        if (!Strings.isNullOrEmpty(etag)) {
            response.setHeader("Etag", etag);
        }
    }
}
