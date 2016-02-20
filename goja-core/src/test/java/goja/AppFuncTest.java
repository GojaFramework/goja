/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja;

import com.google.common.collect.Lists;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import goja.core.Func;

/**
 * <p> . </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-02-16 21:55
 * @since JDK 1.6
 */
public class AppFuncTest {

    @Test
    public void testCommaJoiner() throws Exception {
        String abc = "a,b,c";
        List<String> src_strs = Lists.newArrayList("a", "b", "c");
        String dest = Func.COMMA_JOINER.join(src_strs);
        Assert.assertEquals(abc, dest);
    }

    @Test
    public void testCommaSplitter() throws Exception {
        String abc = "a,b,c";
        List<String> src_strs = Lists.newArrayList("a", "b", "c");
        List<String> dest = Func.COMMA_SPLITTER.splitToList(abc);
        Assert.assertEquals(src_strs, dest);
    }

    @Test
    public void testDashJoiner() throws Exception {
        String abc = "a-b-c";
        List<String> src_strs = Lists.newArrayList("a", "b", "c");
        String dest = Func.DASH_JOINER.join(src_strs);
        Assert.assertEquals(abc, dest);
    }

    @Test
    public void testDashSplitter() throws Exception {

        String abc = "a-b-c";
        List<String> src_strs = Lists.newArrayList("a", "b", "c");
        List<String> dest = Func.DASH_SPLITTER.splitToList(abc);
        Assert.assertEquals(src_strs, dest);
    }

    @Test
    public void testDotJoiner() throws Exception {
        String abc = "a.b.c";
        List<String> src_strs = Lists.newArrayList("a", "b", "c");
        String dest = Func.DOT_JOINER.join(src_strs);
        Assert.assertEquals(abc, dest);
    }

    @Test
    public void testDotSplitter() throws Exception {

        String abc = "a.b.c";
        List<String> src_strs = Lists.newArrayList("a", "b", "c");
        List<String> dest = Func.DOT_SPLITTER.splitToList(abc);
        Assert.assertEquals(src_strs, dest);
    }
}
