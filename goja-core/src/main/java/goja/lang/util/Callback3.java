/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.lang.util;

/**
 * 带三个参数的通用回调接口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @param <T0>
 * @param <T1>
 * @param <T2>
 */
public interface Callback3<T0, T1, T2> {

    void invoke(T0 arg0, T1 arg1, T2 arg2);

}
