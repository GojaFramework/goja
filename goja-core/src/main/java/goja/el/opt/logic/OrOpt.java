/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el.opt.logic;


import goja.exceptions.ElException;
import goja.el.opt.TwoTernary;

/**
 * or(||)
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class OrOpt extends TwoTernary {
    
    public int fetchPriority() {
        return 12;
    }
    public Object calculate() {
        Object lval = calculateItem(left);
        if(!(lval instanceof Boolean)){
            throw new ElException("操作数类型错误!");
        }
        if((Boolean)lval){
            return true;
        }
        Object rval = calculateItem(right);
        if(!(rval instanceof Boolean)){
            throw new ElException("操作数类型错误!");
        }
        return rval;
    }
    public String fetchSelf() {
        return "||";
    }

}
