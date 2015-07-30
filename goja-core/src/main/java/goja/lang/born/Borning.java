/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.lang.born;

/**
 * 对象抽象创建方式
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @param <T>
 */
public interface Borning<T> {

    T born(Object... args);

}
