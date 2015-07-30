/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.castor;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class CastorsTest {
    @Test
    public void testInt() throws Exception {
        final int integer = Castors.me().castTo("563", Integer.class);
        assertEquals(integer,563);

    }


    @Test
    public void testDate() throws Exception {
        Calendar c = Castors.me().castTo("2009-11-12 15:23:12", Calendar.class);
        System.out.println(c);

    }
}