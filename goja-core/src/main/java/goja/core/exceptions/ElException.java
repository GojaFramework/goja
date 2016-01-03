/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core.exceptions;

public class ElException extends RuntimeException {

    private static final long serialVersionUID = -1133638103102657570L;

    public ElException(String message, Throwable cause) {
        super(message, cause);
    }

    public ElException(String format, Object... args) {
        super(String.format(format, args));
    }

    public ElException(String message) {
        super(message);
    }

    public ElException(Throwable cause) {
        super(cause);
    }
}
