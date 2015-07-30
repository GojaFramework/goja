/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.date;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;

/**
 * <p>
 * Date format string constants .
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-11-15 16:29
 * @since JDK 1.6
 */
public interface DateFormatter {

    /**
     * such as  20141010 date.
     */
    String YYYYMMDD = "yyyyMMdd";

    /**
     * such as 20141010 12:12:12
     */
    String YYYYMMDD_HHMMSS = "yyyyMMdd HH:mm:ss";

    /**
     * (date) (month) (year)
     */
    String YYYY_MM_DD = "yyyy-MM-dd";

    /**
     * yyyy-MM-dd HH:mm formatter string.
     */
    String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

    /**
     * yyyy-MM-dd HH:mm:ss formatter string.
     */
    String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";


    /**
     * Date format method
     * <pre>
     *     yyyy-MM-dd HH:mm
     * </pre>
     */
    SimpleDateFormat DATE_FORMAT_YYYY_MM_DD_HH_MM = new SimpleDateFormat(DateFormatter.YYYY_MM_DD_HH_MM);

    /**
     * Split second date format when (date) (month) (year)
     * <pre>
     *     yyyy-MM-dd HH:mm:ss
     * </pre>
     */
    SimpleDateFormat DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat(DateFormatter.YYYY_MM_DD_HH_MM_SS);

    /**
     * The date of the Joda format method
     * <pre>
     *     yyyy-MM-dd HH:mm:ss
     * </pre>
     */
    DateTimeFormatter DATE_TIME_PATTERN_YYYY_MM_DD_HH_MM_SS = DateTimeFormat.forPattern(DateFormatter.YYYY_MM_DD_HH_MM_SS);

    /**
     * The date of the date format in formatting objects.
     * <pre>
     *     yyyy-MM-dd HH:mm
     * </pre>
     */
    DateTimeFormatter DATE_TIME_PATTERN_YYYY_MM_DD_HH_MM = DateTimeFormat.forPattern(DateFormatter.YYYY_MM_DD_HH_MM);

    /**
     * The date time format joda - time format.
     * <pre>
     *     yyyy-MM-dd
     * </pre>
     */
    DateTimeFormatter DATE_TIME_PATTERN_YYYY_MM_DD = DateTimeFormat.forPattern(DateFormatter.YYYY_MM_DD);
}
