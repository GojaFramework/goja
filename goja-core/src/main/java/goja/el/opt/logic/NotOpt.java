/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el.opt.logic;

import goja.exceptions.ElException;
import goja.el.opt.AbstractOpt;

import java.util.Queue;


/**
 * Not(!)
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class NotOpt extends AbstractOpt {
    private Object right;
    
    public int fetchPriority() {
        return 7;
    }
    public void wrap(Queue<Object> rpn){
        right = rpn.poll();
    }
    
    public Object calculate() {
        Object rval = calculateItem(this.right);
        if(rval instanceof Boolean){
            return !(Boolean) rval;
        }
        throw new ElException("'!'操作符操作失败!");
    }
    
    public String fetchSelf() {
        return "!";
    }
}
