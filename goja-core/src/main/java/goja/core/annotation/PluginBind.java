/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * 自定义插件绑定注解.
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-06-24 23:21
 * @since JDK 1.6
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface PluginBind {

    /**
     * 是否忽略
     *
     * @return true 表示忽略,默认不忽略
     */
    boolean ignored() default false;
}
