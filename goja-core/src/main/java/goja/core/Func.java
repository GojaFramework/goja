/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core;

import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.Map;

/**
 * <p>
 * .
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-06-01 20:22
 * @since JDK 1.6
 */
public interface Func {

    /**
     * Map JSON serialized objects
     */
    TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<Map<String, Object>>() {};
    /**
     * Will be set into a comma separated string.
     */
    Joiner                             COMMA_JOINER       = Joiner.on(StringPool.COMMA).skipNulls();

    /**
     * Comma-separated string into a collection of instances.
     */
    Splitter COMMA_SPLITTER = Splitter.on(StringPool.COMMA).trimResults().omitEmptyStrings();

    /**
     *
     */
    Joiner DOT_JOINER = Joiner.on(StringPool.DOT).skipNulls();


    Splitter DOT_SPLITTER  = Splitter.on(StringPool.DOT).trimResults().omitEmptyStrings();
    /**
     *
     */
    Joiner   EQUALS_JOINER = Joiner.on(StringPool.EQUALS).skipNulls();


    Splitter EQUALS_SPLITTER = Splitter.on(StringPool.EQUALS).trimResults().omitEmptyStrings();


    Joiner DASH_JOINER = Joiner.on(StringPool.DASH).skipNulls();

    Splitter DASH_SPLITTER = Splitter.on(StringPool.DASH).trimResults().omitEmptyStrings();


    /**
     * The default database main key.
     *
     * @deprecated plase use {@link StringPool#PK_COLUMN}
     */
    String TABLE_PK_COLUMN = StringPool.PK_COLUMN;


    // ---------------------------------------------------------------- array

    String[] EMPTY_ARRAY = new String[0];
}
