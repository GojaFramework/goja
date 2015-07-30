/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el.opt.object;

import goja.el.opt.AbstractOpt;

import java.util.Queue;

/**
 * ']',数组封装.
 * 本身没做什么操作,只是对'[' ArrayOpt 做了一个封装而已
 *
 * @author juqkai(juqkai@gmail.com)
 */
public class FetchArrayOpt extends AbstractOpt {
    private Object left;

    public void wrap(Queue<Object> operand) {
        left = operand.poll();
    }

    public int fetchPriority() {
        return 1;
    }

    public Object calculate() {
        if (left instanceof ArrayOpt) {
            return ((ArrayOpt) left).calculate();
        }
        return null;
    }

    public String fetchSelf() {
        return "]";
    }
}
