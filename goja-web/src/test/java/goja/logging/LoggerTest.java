package goja.logging;

import goja.core.StringPool;
import goja.core.app.GojaConfig;
import goja.mvc.AjaxSimple;

import org.apache.commons.lang3.StringUtils;
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

    @Test
    public void testOK() throws Exception {

        final String simpleName = AjaxSimple.class.getSimpleName();
        System.out.println("simpleName = " + simpleName);

    }

    @Test
    public void testControllerKey() throws Exception {
        String appPackPrefix = "com.mo008";
        String controllerKey = "/hello";
        String packName = "com.mo008.sys.controllers.admin.log.cc";
        String controllersFlag = "controllers";

        final String removePrefixPack = StringUtils.replace(packName, appPackPrefix, StringPool.EMPTY);
        final String removeControllerPack = StringUtils.replace(removePrefixPack, StringPool.DOT + controllersFlag, StringPool.EMPTY);
        String conkey =  StringUtils.replace(removeControllerPack, StringPool.DOT, StringPool.SLASH) + controllerKey;
        System.out.println("conkey = " + conkey);

    }
}