/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core.kits.lang;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class DateKitTest {

    @Test
    public void testFormatDashYMDHMS() throws Exception {
        assertEquals("1970-01-01 00:00:00", DateKit.formatDashYMDHMS(new Date(0L)));
        assertEquals("2012-09-05 09:57:57", DateKit.formatDashYMDHMS(new Date(1346839077523L)));
    }

    @Test
    public void testFormatForHttpHeaderDate() {
        //some simple tests:
        assertEquals("Thu, 01 Jan 1970 00:00:00 GMT", DateKit.formatForHttpHeader(new Date(0L)));
        assertEquals("Wed, 05 Sep 2012 09:57:57 GMT", DateKit.formatForHttpHeader(new Date(1346839077523L)));
    }

    @Test
    public void testFormatForHttpHeaderLong() {
        //some simple tests:
        assertEquals("Thu, 01 Jan 1970 00:00:00 GMT", DateKit.formatForHttpHeader(0L));
        assertEquals("Wed, 05 Sep 2012 09:57:57 GMT", DateKit.formatForHttpHeader(1346839077523L));

    }

    @Test
    public void testParseHttpDateFormat() throws Exception {
        //some simple tests:
        assertEquals(new Date(0L).toString(), DateKit.parseHttpDateFormat("Thu, 01 Jan 1970 00:00:00 GMT").toString());
        assertEquals(new Date(1346839077523L).toString(), DateKit.parseHttpDateFormat("Wed, 05 Sep 2012 09:57:57 GMT").toString());

    }


    @Test
    public void testParseHttpDateFormatToDateTime() throws Exception {
        //some simple tests:
        assertEquals(new DateTime(0L).toDate().toString(), DateKit.parseHttpDateFormatToDateTime("Thu, 01 Jan 1970 00:00:00 GMT").toDate().toString());
        assertEquals(new DateTime(1346839077523L).toDate().toString(), DateKit.parseHttpDateFormatToDateTime("Wed, 05 Sep 2012 09:57:57 GMT").toDate().toString());

    }
}