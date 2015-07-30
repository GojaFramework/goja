/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el.opt.bit;


import goja.el.opt.TwoTernary;

/**
 * 或
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class BitOr extends TwoTernary {
    public int fetchPriority() {
        return 10;
    }
    public Object calculate() {
        Integer lval = (Integer) calculateItem(left);
        Integer rval = (Integer) calculateItem(right);
        return lval | rval;
    }
    public String fetchSelf() {
        return "|";
    }
}
