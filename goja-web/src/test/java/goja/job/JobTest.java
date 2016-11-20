// /*
//  * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
//  *
//  * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
//  */

// package goja.job;

// import goja.GojaConfig;
// import goja.lang.Lang;
// import org.junit.Before;
// import org.junit.Test;

// import static org.junit.Assert.*;

// public class JobTest {

//     @Before
//     public void setUp() throws Exception {
//         GojaConfig.getConfigProps();
//         new JobsPlugin();

//     }

//     @Test
//     public void testNow() throws Exception {
//         Job<String> job = new TestJob<String>();
//         job.now();
//         Thread.sleep(10000);
//     }

//     public static class TestJob<String> extends Job<String>{
//         @Override
//         public void doJob() throws Exception {
//             System.out.println("lastRun = " + lastRun);
//             super.doJob();
//         }
//     }

// }
