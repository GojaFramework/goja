/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el.opt.arithmetic;

import goja.exceptions.ElException;
import goja.el.opt.AbstractOpt;

import java.util.Queue;


/**
 * "("
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class LBracketOpt extends AbstractOpt {
    public String fetchSelf() {
        return "(";
    }
    public int fetchPriority() {
        return 100;
    }
    
    public void wrap(Queue<Object> obj) {
        throw new ElException("'('符号不能进行wrap操作!");
    }
    public Object calculate() {
        throw new ElException("'('符号不能进行计算操作!");
    }
}
