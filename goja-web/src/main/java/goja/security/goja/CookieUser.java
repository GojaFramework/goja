/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.security.goja;

import java.io.Serializable;

/**
 * <p> . </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-02-12 22:20
 * @since JDK 1.6
 */
public class CookieUser implements Serializable {

    private static final long serialVersionUID = -7375570593653130438L;

    private final long id;

    private final String password;

    private final boolean blocked;

    public CookieUser(long id, String password, boolean blocked) {
        this.id = id;
        this.password = password;
        this.blocked = blocked;
    }

    public long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public boolean isBlocked() {
        return blocked;
    }
}
