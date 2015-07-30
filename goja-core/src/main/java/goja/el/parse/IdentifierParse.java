/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el.parse;


import goja.el.Parse;
import goja.el.obj.AbstractObj;
import goja.el.obj.IdentifierObj;

/**
 * 标识符转换
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class IdentifierParse implements Parse {

    public Object fetchItem(CharQueue exp) {
        StringBuilder sb = new StringBuilder();
        if(Character.isJavaIdentifierStart(exp.peek())){
            sb.append(exp.poll());
            while(!exp.isEmpty() && Character.isJavaIdentifierPart(exp.peek())){
                sb.append(exp.poll());
            }
            if(sb.toString().equals("null")){
                return new IdentifierObj(null);
            }
            if(sb.toString().equals("true")){
                return Boolean.TRUE;
            }
            if(sb.toString().equals("false")){
                return Boolean.FALSE;
            }
            return new AbstractObj(sb.toString());
        }
        return nullobj;
    }

}
