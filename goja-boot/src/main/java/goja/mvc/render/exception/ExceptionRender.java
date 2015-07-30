/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.mvc.render.exception;

import com.jfinal.render.Render;

public abstract class ExceptionRender extends Render {
    private static final long serialVersionUID = -7908640392524128432L;
    private Exception exception;

    public Exception getException() {
        return exception;
    }

    public ExceptionRender setException(Exception exception) {
        this.exception = exception;
        return this;
    }

}
