/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el.opt.object;

import goja.el.opt.AbstractOpt;

import java.util.Queue;


/**
 * 方法执行
 * 以方法体右括号做为边界
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class InvokeMethodOpt extends AbstractOpt {
    private Object left;

    public int fetchPriority() {
        return 1;
    }

    public Object calculate() {
        if(left instanceof MethodOpt){
            return ((MethodOpt) left).calculate();
        }
        return null;
    }

    public String fetchSelf() {
        return "method invoke";
    }

    public void wrap(Queue<Object> operand) {
        left = operand.poll();
    }

}
