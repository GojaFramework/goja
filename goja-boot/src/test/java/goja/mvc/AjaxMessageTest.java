/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.mvc;

import goja.core.StringPool;
import goja.core.tuples.Pair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AjaxMessageTest {

    @Test
    public void testOk() throws Exception {

        AjaxMessage ok = AjaxMessage.OK;
        assertEquals(ok.getMessage(), StringPool.EMPTY);

    }

    @Test
    public void testOk1() throws Exception {

        AjaxMessage ok = AjaxMessage.ok(StringPool.AND);
        assertEquals(ok.getMessage(), StringPool.AND);
    }

    @Test
    public void testOk2() throws Exception {
        Pair<Integer, Integer> data = Pair.with(1,2);
        AjaxMessage ok = AjaxMessage.ok(data);
        assertEquals(ok.getMessage(), StringPool.EMPTY);
    }

    @Test
    public void testOk3() throws Exception {

    }

    @Test
    public void testDeveloping() throws Exception {

    }

    @Test
    public void testNodata() throws Exception {

    }

    @Test
    public void testNodata1() throws Exception {

    }

    @Test
    public void testNodata2() throws Exception {

    }

    @Test
    public void testNologin() throws Exception {

    }

    @Test
    public void testNologin1() throws Exception {

    }

    @Test
    public void testForbidden() throws Exception {

    }

    @Test
    public void testForbidden1() throws Exception {

    }

    @Test
    public void testError() throws Exception {

    }

    @Test
    public void testError1() throws Exception {

    }

    @Test
    public void testError2() throws Exception {

    }

    @Test
    public void testError3() throws Exception {

    }

    @Test
    public void testError4() throws Exception {

    }

    @Test
    public void testFailure() throws Exception {

    }

    @Test
    public void testFailure1() throws Exception {

    }

    @Test
    public void testFailure2() throws Exception {

    }

    @Test
    public void testFailure3() throws Exception {

    }

    @Test
    public void testGetData() throws Exception {

    }

    @Test
    public void testGetMessage() throws Exception {

    }

    @Test
    public void testGetStatus() throws Exception {

    }

    @Test
    public void testToJSON() throws Exception {

    }

    @Test
    public void testGetException() throws Exception {

    }
}