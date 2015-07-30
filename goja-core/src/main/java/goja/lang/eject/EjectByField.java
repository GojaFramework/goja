/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.lang.eject;

import goja.lang.Lang;

import java.lang.reflect.Field;

public class EjectByField implements Ejecting {


    private Field field;

    public EjectByField(Field field) {
        this.field = field;
        this.field.setAccessible(true);
    }

    public Object eject(Object obj) {
        try {
            return null == obj ? null : field.get(obj);
        } catch (Exception e) {
            throw Lang.makeThrow("Fail to get field %s.'%s' because [%s]: %s",
                    field.getDeclaringClass().getName(),
                    field.getName(),
                    Lang.unwrapThrow(e),
                    Lang.unwrapThrow(e).getMessage());
        }
    }

}
