/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el.obj;


/**
 * 标识符对象,即所有非数字,非字符串的对象.
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class IdentifierObj extends AbstractObj{

    public IdentifierObj(String val) {
        super(val);
    }
}