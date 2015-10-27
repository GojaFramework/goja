/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core.exceptions;

/**
 * Error while sending an email
 */
public class MailException extends GojaException {


    private static final long serialVersionUID = -5300552095279529931L;

    public MailException(String message) {
        super(message, null);
    }

    public MailException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorTitle() {
        return "Mail error";
    }

    @Override
    public String getErrorDescription() {
        return String.format("A mail error occured : <strong>%s</strong>", getMessage());
    }


}
