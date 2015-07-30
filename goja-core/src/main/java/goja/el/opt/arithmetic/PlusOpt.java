/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el.opt.arithmetic;


import goja.el.opt.TwoTernary;

/**
 * "+"
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class PlusOpt extends TwoTernary {
    public int fetchPriority() {
        return 4;
    }

    public String fetchSelf() {
        return "+";
    }
    public Object calculate() {
        Object lval = calculateItem(this.left);
        Object rval = calculateItem(this.right);
        
        if(lval instanceof String || rval instanceof String){
            return lval.toString() + rval.toString();
        }
        
        Number nlval = (Number) lval;
        Number nrval = (Number) rval;
        
        if(nrval instanceof Double || nlval instanceof Double){
            return nlval.doubleValue() + nrval.doubleValue();
        }
        if(nrval instanceof Float || nlval instanceof Float){
            return nlval.floatValue() + nrval.floatValue();
        }
        if(nrval instanceof Long || nlval instanceof Long){
            return nlval.longValue() + nrval.longValue();
        }
        return nlval.intValue() + nrval.intValue();
    }

}
