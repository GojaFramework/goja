package goja.logging;

import ch.qos.logback.classic.LoggerContext;
import com.jfinal.kit.PathKit;
import goja.core.app.GojaConfig;
import goja.core.StringPool;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class LoggerInit {
    private static final String slf4jPath = GojaConfig.getProperty("logger.config", "/logback.xml");

    public static void init(){
        URL slf4jConf = LoggerInit.class.getResource(slf4jPath);
        final String app_name = GojaConfig.getAppName();
        final String app_version = GojaConfig.getVersion();
        if (slf4jConf == null) {
            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            lc.reset();
            AppLogConfigurator.configure(lc);

            Logger.slf4j = LoggerFactory.getLogger(app_name + StringPool.AT + app_version);
        } else if (Logger.slf4j == null) {

            if (slf4jConf.getFile().indexOf(PathKit.getWebRootPath()) == 0) {
                Logger.configuredManually = true;
            }
            Logger.slf4j = LoggerFactory.getLogger(app_name + StringPool.AT + app_version);

        }
    }
}
