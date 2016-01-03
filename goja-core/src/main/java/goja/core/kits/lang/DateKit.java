/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core.kits.lang;

import goja.core.date.DateFormatter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.Locale;

/**
 * <p>
 * .
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-11-04 16:34
 * @since JDK 1.6
 */
public class DateKit {
    /**
     * From here: http://www.ietf.org/rfc/rfc1123.txt
     */
    private static final DateTimeFormatter RFC1123_DATE_FORMAT = DateTimeFormat
            .forPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'")
            .withLocale(Locale.US)
            .withZone(DateTimeZone.UTC);
    private static final DateTimeFormatter YMDHMS_DASH_DATE_FORMAT = DateTimeFormat
            .forPattern(DateFormatter.YYYY_MM_DD_HH_MM_SS)
            .withLocale(Locale.US)
            .withZone(DateTimeZone.UTC);
    private static final DateTimeFormatter YMD_DASH_DATE_FORMAT = DateTimeFormat
            .forPattern(DateFormatter.YYYY_MM_DD)
            .withLocale(Locale.US)
            .withZone(DateTimeZone.UTC);
    private static final DateTimeFormatter YMDHM_DASH_DATE_FORMAT = DateTimeFormat
            .forPattern(DateFormatter.YYYY_MM_DD_HH_MM)
            .withLocale(Locale.US)
            .withZone(DateTimeZone.UTC);

    private DateKit() {
    }

    /**
     * Can be used to format a date into http header compatible
     * strings.
     * <p/>
     * It can be used to generate something like:
     * Date: 2014-11-12 10:10:10
     * Expires: 2014-11-12 10:10:10
     *
     * @param date The date to format
     * @return a http header compatible string like "Thu, 01 Jan 1970 00:00:00 GMT"
     */
    public static String formatDashYMDHMS(Date date) {
        return YMDHMS_DASH_DATE_FORMAT.print(new DateTime(date));
    }

    /**
     * Can be used to parse http times. For instance something like a http header
     * Date: 2014-11-12 10:10:10
     * <p/>
     * INFO: consider the JodaTime based DateUtil.parseHttpDateFormatToDateTime(...) version
     *
     * @param httpDateFormat in http format: Date: Tue, 26 Mar 2013 13:47:13 GMT
     * @return A nice "Date" object containing that http timestamp.
     * @throws IllegalArgumentException If something goes wrong.
     */
    public static Date parseDashYMDHMS(String httpDateFormat) throws IllegalArgumentException {

        return parseDashYMDHMSDateTime(httpDateFormat).toDate();
    }

    /**
     * Can be used to parse http times. For instance something like a http header
     * Date: 2014-11-12 10:10:10
     *
     * @param httpDateFormat in http format: Date: Tue, 26 Mar 2013 13:47:13 GMT
     * @return A nice "DateTime" (JodaTime) object containing that http timestamp.
     * @throws IllegalArgumentException If something goes wrong.
     */
    public static DateTime parseDashYMDHMSDateTime(String httpDateFormat) throws IllegalArgumentException {

        return YMDHMS_DASH_DATE_FORMAT.parseDateTime(httpDateFormat);

    }

    /**
     * @param date
     * @return
     */
    public static String formatDashYMD(Date date) {
        return YMD_DASH_DATE_FORMAT.print(new DateTime(date));
    }

    public static Date parseDashYMD(String httpDateFormat) throws IllegalArgumentException {

        return parseDashYMDDateTime(httpDateFormat).toDate();
    }

    public static DateTime parseDashYMDDateTime(String httpDateFormat) throws IllegalArgumentException {

        return YMD_DASH_DATE_FORMAT.parseDateTime(httpDateFormat);

    }

    /**
     * @param date
     * @return
     */
    public static String formatDashYMDHM(Date date) {
        return YMDHM_DASH_DATE_FORMAT.print(new DateTime(date));
    }

    public static Date parseDashYMDHM(String httpDateFormat) throws IllegalArgumentException {

        return parseDashYMDHMDateTime(httpDateFormat).toDate();
    }

    public static DateTime parseDashYMDHMDateTime(String httpDateFormat) throws IllegalArgumentException {

        return YMDHM_DASH_DATE_FORMAT.parseDateTime(httpDateFormat);

    }


    /**
     * Can be used to format a date into http header compatible
     * strings.
     * <p/>
     * It can be used to generate something like:
     * Date: Wed, 05 Sep 2012 09:16:19 GMT
     * Expires: Thu, 01 Jan 1970 00:00:00 GMT
     *
     * @param date The date to format
     * @return a http header compatible string like "Thu, 01 Jan 1970 00:00:00 GMT"
     */
    public static String formatForHttpHeader(Date date) {
        return RFC1123_DATE_FORMAT.print(new DateTime(date));
    }

    /**
     * Can be used to format a unix timestamp into
     * http header compatible strings.
     * <p/>
     * It can be used to generate something like:
     * Date: Wed, 05 Sep 2012 09:16:19 GMT
     * Expires: Thu, 01 Jan 1970 00:00:00 GMT
     *
     * @param unixTime The long (unixtime) to format
     * @return a http header compatible string like "Thu, 01 Jan 1970 00:00:00 GMT"
     */
    public static String formatForHttpHeader(Long unixTime) {

        return RFC1123_DATE_FORMAT.print(new DateTime(unixTime));
    }


    /**
     * Can be used to parse http times. For instance something like a http header
     * Date: Tue, 26 Mar 2013 13:47:13 GMT
     * <p/>
     * INFO: consider the JodaTime based DateUtil.parseHttpDateFormatToDateTime(...) version
     *
     * @param httpDateFormat in http format: Date: Tue, 26 Mar 2013 13:47:13 GMT
     * @return A nice "Date" object containing that http timestamp.
     * @throws IllegalArgumentException If something goes wrong.
     */
    public static Date parseHttpDateFormat(String httpDateFormat) throws IllegalArgumentException {

        return parseHttpDateFormatToDateTime(httpDateFormat).toDate();

    }

    /**
     * Can be used to parse http times. For instance something like a http header
     * Date: Tue, 26 Mar 2013 13:47:13 GMT
     *
     * @param httpDateFormat in http format: Date: Tue, 26 Mar 2013 13:47:13 GMT
     * @return A nice "DateTime" (JodaTime) object containing that http timestamp.
     * @throws IllegalArgumentException If something goes wrong.
     */
    public static DateTime parseHttpDateFormatToDateTime(String httpDateFormat) throws IllegalArgumentException {

        return RFC1123_DATE_FORMAT.parseDateTime(httpDateFormat);

    }


    /**
     * Get  days after UnixTime
     *
     * @param day days
     * @return unixtime
     */
    public static Long getUnixTimeAfterDay(int day) {
        return day * 24 * 60 * 60L + System.currentTimeMillis() / 1000;
    }

    /**
     * Get  days before UnixTime
     *
     * @param day days
     * @return unixtime
     */
    public static Long getUnixTimeBeforeDay(int day) {
        return System.currentTimeMillis() / 1000 - day * 24 * 60 * 60L;
    }

    /**
     * Get UnixTime current time
     *
     * @return UnixTime
     */
    public static Long getCurrentUnixTime() {
        return System.currentTimeMillis() / 1000;
    }


}
