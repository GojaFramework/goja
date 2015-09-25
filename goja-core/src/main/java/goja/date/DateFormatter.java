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
    SimpleDateFormat DP_YYYY_MM_DD_HH_MM = new SimpleDateFormat(DateFormatter.YYYY_MM_DD_HH_MM);

    /**
     * The date of the Joda format method
     * <pre>
     *     yyyy-MM-dd HH:mm:ss
     * </pre>
     */
    DateTimeFormatter DTP_YYYY_MM_DD_HH_MM_SS = DateTimeFormat.forPattern(DateFormatter.YYYY_MM_DD_HH_MM_SS);

}
