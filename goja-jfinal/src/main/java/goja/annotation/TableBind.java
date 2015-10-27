/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.annotation;

import com.jfinal.plugin.activerecord.DbKit;
import goja.core.StringPool;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
    String[] pks() default StringPool.PK_COLUMN;

    /**
     * ignore flag
     *
     * @return ignore flag.
     */
    boolean ignore() default false;
}
