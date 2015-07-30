/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el.opt.bit;

import goja.el.opt.AbstractOpt;

import java.util.Queue;


/**
 * 非
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class BitNot extends AbstractOpt {
    private Object right;
    public int fetchPriority() {
        return 2;
    }
    public void wrap(Queue<Object> operand) {
        right = operand.poll();
    }
    public Object calculate() {
        Integer rval = (Integer) calculateItem(right);
        return ~rval;
    }
    public String fetchSelf() {
        return "~";
    }
}
