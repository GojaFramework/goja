/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core.libs;

import goja.core.app.GojaConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p> . </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-06-02 23:34
 * @since JDK 1.6
 */
public class Expression {
    static Pattern expression = Pattern.compile("^\\$\\{(.*)\\}$");

    public static Object evaluate(String value, String defaultValue) {
        Matcher matcher = expression.matcher(value);
        if (matcher.matches()) {
            return GojaConfig.getProperty(matcher.group(1), defaultValue);
        }
        return value;
    }
}
