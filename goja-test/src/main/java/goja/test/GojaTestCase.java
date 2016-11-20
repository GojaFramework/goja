package goja.test;

import goja.Goja;
import goja.core.app.GojaConfig;
import goja.core.kits.reflect.Reflect;

import org.junit.BeforeClass;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public abstract class GojaTestCase {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        GojaConfig.getConfigProps();
        Reflect.on(Goja.class).call("initWithTest");
    }
}
