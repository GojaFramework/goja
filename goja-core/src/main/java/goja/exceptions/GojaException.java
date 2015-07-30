/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.exceptions;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The super class for all Play! exceptions
 */
public abstract class GojaException extends RuntimeException {

    private static final long       serialVersionUID = -1849525598831557172L;
    static               AtomicLong atomicLong       = new AtomicLong(System.currentTimeMillis());
    String id;

    public GojaException() {
        super();
        setId();
    }

    public GojaException(String message) {
        super(message);
        setId();
    }

    public GojaException(String message, Throwable cause) {
        super(message, cause);
        setId();
    }

    void setId() {
        long nid = atomicLong.incrementAndGet();
        id = Long.toString(nid, 26);
    }

    public abstract String getErrorTitle();

    public abstract String getErrorDescription();


    public Integer getLineNumber() {
        return -1;
    }

    public String getSourceFile() {
        return "";
    }

    public String getId() {
        return id;
    }


    public String getMoreHTML() {
        return null;
    }
}