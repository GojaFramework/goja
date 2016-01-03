/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core.exceptions;

/**
 * <p> . </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-03-28 12:08
 * @since JDK 1.6
 */
public class DatabaseException extends GojaException {

    public DatabaseException() {
    }

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorTitle() {
        return "The Database jdbc error!";
    }

    @Override
    public String getErrorDescription() {
        return "The Database jdbc error!";
    }
}
