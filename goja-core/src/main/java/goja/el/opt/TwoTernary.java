/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el.opt;

import java.util.Queue;

/**
 * 二元运算,只是提取了公共部分
 * 
 * @author juqkai(juqkai@gmail.com)
 * 
 */
public abstract class TwoTernary extends AbstractOpt {
    protected Object right;
    protected Object left;

    public void wrap(Queue<Object> rpn) {
        right = rpn.poll();
        left = rpn.poll();
    }

    public Object getRight() {
        return calculateItem(right);
    }

    public Object getLeft() {
        return calculateItem(left);
    }
}
