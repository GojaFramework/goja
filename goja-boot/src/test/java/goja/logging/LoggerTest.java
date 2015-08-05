package goja.logging;

import goja.Goja;
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
        Goja.mode = Goja.Mode.PROD;
        LoggerInit.init();
    }


    @Test
    public void testInfo() throws Exception {
        for (int i = 0; i < 1000; i++) {
            Logger.info("test index os ... {}", i);
        }

    }
}