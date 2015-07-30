/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el;


import goja.lang.util.Context;

/**
 * EL参数缓存,用于预编译.<br>
 * 相当于一个回调,因为最初把这个类存入 Elobj 的时候,context属性并没有相应的值.
 * 而当外面传入了context后, Elobj 才能从这个类中取得相应的值.
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class ElCache {
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

}
