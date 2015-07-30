/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.lang.eject;

import java.util.Map;

public class EjectFromMap implements Ejecting {

    private String key;

    public EjectFromMap(String key) {
        this.key = key;
    }

    public Object eject(Object obj) {
        return null == obj ? null : ((Map<?, ?>) obj).get(key);
    }

}
