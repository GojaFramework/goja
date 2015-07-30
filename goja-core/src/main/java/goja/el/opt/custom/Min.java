/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el.opt.custom;

import goja.el.opt.RunMethod;

import java.util.List;


/**
 * 取小
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class Min implements RunMethod {
    public Object run(List<Object> param) {
        if(param.size() <= 0){
            return null;
        }
        Number n1 = (Number) param.get(0);
        for(int i = 1; i < param.size(); i ++){
            Number n2 = (Number) param.get(i);
            n1 = (Number) min(n1, n2);
        }
        return n1;
    }
    
    private Object min(Number n1, Number n2){
        if(n1 == null){
            return n2;
        }
        if(n2 == null){
            return n1;
        }
        if(n1 instanceof Double || n2 instanceof Double){
            return Math.min(n1.doubleValue(), n2.doubleValue());
        }
        if(n1 instanceof Float || n2 instanceof Float){
            return Math.min(n1.floatValue(), n2.floatValue());
        }
        if(n1 instanceof Long || n2 instanceof Long){
            return Math.min(n1.longValue(), n2.longValue());
        }
        return Math.min(n1.intValue(), n2.intValue());
    }

    public boolean canWork() {
        return true;
    }

    public String fetchSelf() {
        return "min";
    }

}
