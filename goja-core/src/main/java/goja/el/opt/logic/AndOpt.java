/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el.opt.logic;


import goja.exceptions.ElException;
import goja.el.opt.TwoTernary;

/**
 * and
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class AndOpt extends TwoTernary {
    public int fetchPriority() {
        return 11;
    }
    
    public Object calculate() {
        Object lval = calculateItem(this.left);
        if(!(lval instanceof Boolean)){
            throw new ElException("操作数类型错误!");
        }
        if(!(Boolean)lval){
            return false;
        }
        Object rval = calculateItem(this.right);
        if(!(rval instanceof Boolean)){
            throw new ElException("操作数类型错误!");
        }
        if(!(Boolean)rval){
            return false;
        }
        return true;
    }

    public String fetchSelf() {
        return "&&";
    }

}
