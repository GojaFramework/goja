/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.lang;

@SuppressWarnings("serial")
public class FailToSetValueException extends RuntimeException {

    public FailToSetValueException(String message, Throwable e) {
        super(message, e);
    }

}
