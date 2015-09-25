package goja.test;

import goja.Goja;
import goja.config.GojaConfig;
import goja.initialize.ctxbox.ClassFinder;
import goja.kits.reflect.Reflect;
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
        ClassFinder.findWithTest();
        Reflect.on(Goja.class).call("initWithTest");

    }
}
