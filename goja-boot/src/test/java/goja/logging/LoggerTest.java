package goja.logging;

import goja.core.app.GojaConfig;
import org.junit.Before;
import org.junit.Test;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class LoggerTest {


    @Before
    public void setUp() throws Exception {
        GojaConfig.init();
        LoggerInit.init();
    }


    @Test
    public void testInfo() throws Exception {
        for (int i = 0; i < 1000; i++) {
            Logger.info("test index os ... {}", i);
        }

    }
}