// /*
//  * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
//  *
//  * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
//  */

// package goja.jobs;

// import goja.GojaConfig;
// import goja.Goja;
// import goja.init.ctxbox.ClassFinder;
// import goja.job.JobsPlugin;
// import org.junit.After;
// import org.junit.Before;
// import org.junit.Test;

// public class JobsPluginTest {

//     static JobsPlugin jobsPlugin;

//     @Before
//     public void setUp() throws Exception {
//         ClassFinder.findWithTest();
//         GojaConfig.getConfigProps();
//         Goja.configuration = GojaConfig.getConfigProps();
//         jobsPlugin = new JobsPlugin();
//     }

//     @Test
//     public void testApplicationStart() throws Exception {
//         new Thread(new Runnable() {
//             @Override
//             public void run() {

//                 jobsPlugin.start();
//             }
//         }).start();
//         Thread.currentThread().sleep(1000000);
//     }

//     @Test
//     public void testNowRun() throws Exception {

//     }

//     @After
//     public void tearDown() throws Exception {
//         jobsPlugin.stop();

//     }
// }
