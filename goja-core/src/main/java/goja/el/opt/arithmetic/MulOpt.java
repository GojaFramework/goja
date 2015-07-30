/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el.opt.arithmetic;


import goja.el.opt.TwoTernary;

/**
 * 乘
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class MulOpt extends TwoTernary {
    public int fetchPriority() {
        return 3;
    }
    public Object calculate() {
        Number lval = (Number) calculateItem(this.left);
        Number rval = (Number) calculateItem(this.right);
        if(rval instanceof Double || lval instanceof Double){
            return lval.doubleValue() * rval.doubleValue();
        }
        if(rval instanceof Float || lval instanceof Float){
            return lval.floatValue() * rval.floatValue();
        }
        if(rval instanceof Long || lval instanceof Long){
            return lval.longValue() * rval.longValue();
        }
        return lval.intValue() * rval.intValue();
    }

    public String fetchSelf() {
        return "*";
    }

}
