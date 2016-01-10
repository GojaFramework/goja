/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.core.annotation;

import com.jfinal.plugin.activerecord.DbKit;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TableBind {
    /**
     * The Table Name.
     *
     * @return Table Name.
     */
    String tableName();

    /**
     * The DataBase Config.
     *
     * @return DataBase Config.
     */
    String[] configName() default DbKit.MAIN_CONFIG_NAME;

    /**
     * The Table pk fields.
     *
     * @return pks
     */
    String[] pks() default "id";

    /**
     * ignore flag
     *
     * @return ignore flag.
     */
    boolean ignore() default false;
}
