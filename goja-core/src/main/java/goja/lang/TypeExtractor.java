/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.lang;

/**
 * 类型提炼器。针对一个类型，提炼出一组最能反应其特征的类型
 *
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface TypeExtractor {

    Class<?>[] extract(Mirror<?> mirror);
}
