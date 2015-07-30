/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el.opt.logic;


import goja.el.opt.TwoTernary;

/**
 * 等于
 *
 * @author juqkai(juqkai@gmail.com)
 */
public class EQOpt extends TwoTernary {
    public int fetchPriority() {
        return 7;
    }

    public Object calculate() {
        Object lval = calculateItem(this.left);
        Object rval = calculateItem(this.right);
        if (lval == rval) {
            return true;
        }
        return lval.equals(rval);
    }

    public String fetchSelf() {
        return "==";
    }
}
