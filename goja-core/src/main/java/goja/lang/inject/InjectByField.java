/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.lang.inject;

import goja.castor.Castors;
import goja.lang.Lang;

import java.lang.reflect.Field;

public class InjectByField implements Injecting {


    private Field field;

    public InjectByField(Field field) {
        this.field = field;
        this.field.setAccessible(true);
    }

    public void inject(Object obj, Object value) {
        Object v = null;
        try {
            v = Castors.me().castTo(value, field.getType());
            field.set(obj, v);
        } catch (Exception e) {
            throw Lang.makeThrow("Fail to set '%s'[ %s ] to field %s.'%s' because [%s]: %s",
                    value,
                    v,
                    field.getDeclaringClass().getName(),
                    field.getName(),
                    Lang.unwrapThrow(e),
                    Lang.unwrapThrow(e).getMessage());
        }
    }
}
