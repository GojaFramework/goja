/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.mvc;

/**
 * <p>
 * After JFinal start, run class.
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-01-19 22:29
 * @since JDK 1.6
 */
public interface AppLoadEvent {

    /**
     * run method .
     */
    public void load();
}
