// /*
//  * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
//  *
//  * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
//  */

// package goja;

// import goja.test.ControllerTestCase;
// import org.junit.Test;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// /**
//  * <p>
//  * .
//  * </p>
//  *
//  * @author sagyf yang
//  * @version 1.0 2014-02-24 11:02
//  * @since JDK 1.6
//  */
// public class IndexControllerTest extends ControllerTestCase<Goja> {
//     private static final Logger logger = LoggerFactory.getLogger(IndexControllerTest.class);

//     @Test
//     public void testIndex() throws Exception {
//         String url = "/index";
//         String resp = use(url).invoke();
//         logger.info("The index path repsonse {}", resp);
//     }

//     @Test
//     public void testFilter() throws Exception {
//         String url = "/index/filter";
//         String resp = use(url).invoke();
//         System.out.println(resp);

//     }
// }
