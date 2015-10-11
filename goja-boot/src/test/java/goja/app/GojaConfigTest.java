package goja.app;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class GojaConfigTest {

    @Before
    public void setUp() throws Exception {
        GojaConfig.init();
    }


    @Test
    public void testRedis() throws Exception {
        Assert.assertEquals("goja-test-app", GojaConfig.getAppName());
        Assert.assertEquals("0.1", GojaConfig.getVersion());
        Assert.assertEquals("http://127.0.0.1:8080/jfinal-example", GojaConfig.getAppDomain());
    }
}
