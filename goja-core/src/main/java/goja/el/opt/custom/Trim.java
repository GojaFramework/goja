/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el.opt.custom;

import goja.el.opt.RunMethod;
import goja.exceptions.ElException;

import java.util.List;


/**
 * 去掉字符串两边的空格
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class Trim implements RunMethod {
    public Object run(List<Object> fetchParam) {
        if(fetchParam.size() <= 0){
            throw new ElException("trim方法参数错误");
        }
        String obj = (String) fetchParam.get(0);
        return obj.trim();
    }

    public boolean canWork() {
        return true;
    }

    public String fetchSelf() {
        return "trim";
    }
}
