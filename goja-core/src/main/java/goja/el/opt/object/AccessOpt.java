/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el.opt.object;

import goja.exceptions.ElException;
import goja.el.Operator;
import goja.el.obj.Elobj;
import goja.el.opt.RunMethod;
import goja.el.opt.TwoTernary;
import goja.lang.InvokingException;
import goja.lang.Lang;
import goja.lang.Mirror;
import goja.lang.util.Context;

import java.util.List;
import java.util.Map;


/**
 * 访问符:'.'
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class AccessOpt extends TwoTernary implements RunMethod {
    public int fetchPriority() {
        return 1;
    }

    public Object calculate() {
        //如果直接调用计算方法,那基本上就是直接调用属性了吧...我也不知道^^
        Object obj = fetchVar();
        if (obj == null) {
            throw new ElException("obj is NULL, can't call obj." + right);
        }
        if(obj instanceof Map){
            Map<?,?> om = (Map<?, ?>) obj;
            if(om.containsKey(right.toString())){
                return om.get(right.toString());
            }
        }
        if (obj instanceof Context) {
            Context sc = (Context) obj;
            if (sc.has(right.toString())) {
                return sc.get(right.toString());
            }
        }
        
        Mirror<?> me = Mirror.me(obj);
        return me.getValue(obj, right.toString());
    }
    
    public Object run(List<Object> param) {
        Object obj = fetchVar();
        Mirror<?> me = null;
        if (obj == null)
            throw new NullPointerException();
        if (obj instanceof Class) {
            //也许是个静态方法
            me = Mirror.me(obj);
            try {
                return me.invoke(obj, right.toString(), param.toArray());
            } catch (InvokingException e) {
                throw e;
            } catch (Throwable e) {
                if (Lang.unwrapThrow(e) instanceof NoSuchMethodException) {
                    me = Mirror.me(obj.getClass().getClass());
                    return me.invoke(obj, right.toString(), param.toArray());
                }
                throw Lang.wrapThrow(e);
            }
        }
        else {
            me = Mirror.me(obj);
            return me.invoke(obj, right.toString(), param.toArray());
        }
    }
    
    /**
     * 取得变得的值
     */
    public Object fetchVar(){
        if(left instanceof Operator){
            return ((Operator) left).calculate();
        }
        if(left instanceof Elobj){
            return ((Elobj) left).fetchVal();
        }
        return left;
    }

    public String fetchSelf() {
        return ".";
    }
}
