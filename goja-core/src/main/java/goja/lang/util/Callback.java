/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.lang.util;

/**
 * 带一个参数的通用回调接口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @param <T>
 */
public interface Callback<T> {

    void invoke(T obj);

}
