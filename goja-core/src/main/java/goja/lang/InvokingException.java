/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.lang;

import static java.lang.String.format;

@SuppressWarnings("serial")
public class InvokingException extends RuntimeException {

    public InvokingException(String format, Object... args) {
        super(format(format, args));
    }

    public InvokingException(String msg, Throwable cause) {
        super(String.format(msg, cause.getMessage()), cause);
    }
}
