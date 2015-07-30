/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el;

import goja.lang.Lang;
import goja.lang.util.Context;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ElTest {

    @Test
    public void testTest() throws Exception {

        final int eval = El.eval("3+4*5");
        assertEquals(eval, 23);
    }

    @Test
    public void testEvalParam() throws Exception {
        Context context = Lang.context();
        context.set("a", 10);
//        final boolean val = El.eval(context, "a==9 || a != 10");
//        assertEquals(val, true);

    }

    @Test
    public void testEvalObjectParam() throws Exception {
        Context context = Lang.context();
        final ETObj etObj = new ETObj();
        etObj.setBl(true);
        etObj.setNum(1);
        etObj.setStr2("abc");
        etObj.str = "cde";
        context.putAll(etObj);
        final boolean val = El.eval(context, "str=='cde'");
        assertEquals(val, true);

        final boolean strequal = El.eval(context, "str2 == 'abc");
        assertEquals(strequal, true);



    }
}