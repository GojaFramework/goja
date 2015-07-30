///*
// * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
// *
// * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
// */
//
//package goja.plugins.sqlinxml;
//
//import goja.Goja;
//import goja.kits.reflect.Reflect;
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
//public class SqlKitTest {
//    @Before
//    public void setUp() throws Exception {
//
//        Reflect.on(Goja.class).call("initWithTest");
//        SqlKit.init();
//
//    }
//
////    @Test
//    public void testSqlInJar() throws Exception {
//        assertNotNull(SqlKit.sql("app.findByUUID"));
//
//    }
//
//    @Test
//    public void testSql() throws Exception {
//        assertNotNull(SqlKit.sql("test.slqinxml"));
//    }
//
//    @Test
//    public void testClearSqlMap() throws Exception {
//        SqlKit.clearSqlMap();
//        assertNull(SqlKit.sql("test.slqinxml"));
//    }
//
//    @Test
//    public void testPutOver() throws Exception {
//        SqlKit.putOver("abc","123");
//        assertEquals("123", SqlKit.sql("abc"));
//    }
//
//
//    @Test
//    public void testRemove() throws Exception {
//
//        SqlKit.remove("abc");
//        assertNull(SqlKit.sql("abc"));
//    }
//}