package goja.core.app;

import goja.core.Func;
import goja.core.StringPool;
import goja.core.kits.io.ResourceKit;
import com.jfinal.kit.PropKit;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * <p> 属性文件获取，并且当属性文件发生改变时，自动重新加载. <p/> 1. 可配置是否重启应用 </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-01-13 10:24
 * @since JDK 1.6
 */
public final class GojaConfig {

    private static final Logger logger = LoggerFactory.getLogger(GojaConfig.class);

    /**
     * 默认的属性文件
     */
    private static final String APPLICATION_PROP = "application.conf";

    private static Properties configProps;

    /**
     * 运行模式
     */
    private static ApplicationMode applicationMode;

    /**
     * 是否启动身份验证
     */
    private static boolean security;
    /**
     * 身份验证配置文件
     */
    private static String  appSecurityConfig;

    /**
     * 系统版本
     */
    private static String version;
    /**
     * 系统名称
     */
    private static String appName;

    /**
     * 应用domain
     */
    private static String appDomain;
    /**
     * 应用扫描包
     */
    private static List<String> appScans;

    /**
     * 默认数据库连接
     */
    private static String defaultDBUrl;
    /**
     * 默认数据库用户名
     */
    private static String defaultDBUsername;
    /**
     * 默认数据库用户密码
     */
    private static String defaultDBPassword;

    /**
     * 配置文件夹
     */
    private static File configFolderFile;

    /**
     * 默认的视图存放文件夹
     */
    private static String defaultViewPath;

    private static String jsonMode;

    private static boolean initialize = false;
    /**
     * 是否是API服务
     */
    private static boolean api        = false;


    private GojaConfig() {
    }

    public static void init() {
        if (initialize) {
            logger.warn("配置文件已经加载，不需要在调用init()方法！");
            return;
        }

        final Properties p = new Properties();
        ResourceKit.loadFileInProperties(APPLICATION_PROP, p);
        if (checkNullOrEmpty(p)) {
            throw new IllegalArgumentException("Properties file can not be empty. " + APPLICATION_PROP);
        }

        final String configFolder = p.getProperty(GojaPropConst.APPCONFIGFOLDER);
        if (!Strings.isNullOrEmpty(configFolder)) {
            configFolderFile = new File(configFolder);
            if (!configFolderFile.exists()) {
                throw new RuntimeException(
                        "The application config folder " + configFolder + " is not found!");
            }
            configProps =
                    PropKit.use(FileUtils.getFile(configFolderFile, "application.conf")).getProperties();
        } else {
            configProps = p;
        }
        applicationMode = getApplicationModel();
        security = getPropertyToBoolean(GojaPropConst.APPSECURITY, true);
        appSecurityConfig = getProperty(GojaPropConst.APPSECURITYCONFIG, "security.conf");
        if (security) {
            // 如果启用了身份验证，如果配置文件不存在，系统无法启动
            if (!Strings.isNullOrEmpty(configFolder)) {
                final File securityFile = FileUtils.getFile(configFolderFile, appSecurityConfig);
                if (!securityFile.exists()) {
                    throw new RuntimeException("The app security config file [ "
                            + appSecurityConfig
                            + "] not found in ["
                            + configFolder
                            + "]!");
                }
            }
        }
        version = getProperty(GojaPropConst.APPVERSION, "V0.0.1");
        appName = getProperty(GojaPropConst.APPNAME, "application");
        appDomain = getProperty(GojaPropConst.APPDOMAIN, "http://127.0.0.1:8080/" + appName);
        defaultDBUrl = getProperty(GojaPropConst.DBURL);
        defaultDBUsername = getProperty(GojaPropConst.DBUSERNAME, "root");
        defaultDBPassword = getProperty(GojaPropConst.DBPASSWORD, "123456");
        defaultViewPath = GojaConfig.getProperty(GojaPropConst.APP_VIEWPATH,
                File.separator + "WEB-INF" + File.separator + "views");

        String appScan = GojaConfig.getProperty(GojaPropConst.APP_SCAN, "app");
        appScans = Func.COMMA_SPLITTER.splitToList(appScan);
        jsonMode = GojaConfig.getProperty(GojaPropConst.APP_JSON_MODE);
        api = GojaConfig.getPropertyToBoolean(GojaPropConst.APP_API, false);
        initialize = true;
    }


    public static String getDefaultViewPath() {
        return defaultViewPath;
    }

    /**
     * 取得配置的JSON渲染
     *
     * @return 配置的json渲染
     */
    public static String getJsonMode() {
        return jsonMode;
    }

    /**
     * @return 系统版本
     */
    public static String getVersion() {
        return version;
    }

    /**
     * @return 应用名称
     */
    public static String getAppName() {
        return appName;
    }

    /**
     * @return 是否启动身份验证
     */
    public static boolean isSecurity() {
        return security;
    }

    public static String getAppSecurityConfig() {
        return appSecurityConfig;
    }

    public static List<String> getAppScans() {
        return appScans;
    }

    /**
     * 取得系统的运行模式
     *
     * @return 系统运行模式
     */
    public static ApplicationMode getApplicationMode() {
        return applicationMode;
    }

    public static String getAppDomain() {
        return appDomain;
    }

    public static String getDefaultDBUrl() {
        return defaultDBUrl;
    }

    public static String getDefaultDBUsername() {
        return defaultDBUsername;
    }

    public static String getDefaultDBPassword() {
        return defaultDBPassword;
    }

    public static File getConfigFolderFile() {
        return configFolderFile;
    }

    public static Map<String, Properties> loadDBConfig(Properties p) {
        Map<String, Properties> dbConfigs = Maps.newHashMapWithExpectedSize(1);
        for (Object o : p.keySet()) {
            String _key  = String.valueOf(o);
            String value = p.getProperty(_key);

            if (StringUtils.startsWithIgnoreCase(_key, "db")) {
                int last_idx = _key.lastIndexOf(StringPool.DOT);
                if (last_idx > 2) {
                    // like db.second.url
                    String config_name = _key.substring(_key.indexOf(StringPool.DOT) + 1, last_idx);
                    if (logger.isDebugEnabled()) {
                        logger.debug("the db config is {}", config_name);
                    }
                    Properties db_config_props = dbConfigs.get(config_name);
                    if (db_config_props == null) {
                        db_config_props = new Properties();
                        dbConfigs.put(config_name, db_config_props);
                    }
                    _key = _key.replace(StringPool.DOT + config_name, StringPool.EMPTY);
                    db_config_props.put(_key, value);
                } else {
                    Properties db_main_props = dbConfigs.get("main");
                    if (db_main_props == null) {
                        db_main_props = new Properties();
                        dbConfigs.put("main", db_main_props);
                    }
                    db_main_props.put(_key, value);
                }
            }
        }
        return dbConfigs;
    }

//    /**
//     * 重新加载配置文件
//     */
    //    public static void reload() {
    //        configProps.remove();
    //        clear();
    //        readConf();
    //    }

    /**
     * 如果属性文件为空或者没有内容，则返回true
     *
     * @param p 属性信息
     * @return 是否为空或者没有内容
     */
    private static boolean checkNullOrEmpty(Properties p) {
        return p == null || p.isEmpty();
    }

    /**
     * 获取系统的配置
     *
     * @return 系统配置信息
     */
    public static Properties getConfigProps() {
        return configProps;
    }

    public static String getProperty(String key) {
        if (checkNullOrEmpty(configProps)) {
            return StringPool.EMPTY;
        }
        return configProps.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        if (checkNullOrEmpty(configProps)) {
            return defaultValue;
        }
        return configProps.getProperty(key, defaultValue);
    }

    public static Integer getPropertyToInt(String key) {
        Integer resultInt = null;
        if (checkNullOrEmpty(configProps)) {
            return null;
        }
        String resultStr = configProps.getProperty(key);
        if (resultStr != null) {
            resultInt = Ints.tryParse(resultStr);
        }
        return resultInt;
    }

    public static int getPropertyToInt(String key, int defaultValue) {
        return MoreObjects.firstNonNull(getPropertyToInt(key), defaultValue);
    }

    public static Boolean getPropertyToBoolean(String key) {
        if (checkNullOrEmpty(configProps)) {
            return null;
        }
        String  resultStr  = configProps.getProperty(key);
        Boolean resultBool = null;
        if (resultStr != null) {
            if (resultStr.trim().equalsIgnoreCase("true")) {
                resultBool = true;
            } else if (resultStr.trim().equalsIgnoreCase("false")) {
                resultBool = false;
            }
        }
        return resultBool;
    }

    public static boolean getPropertyToBoolean(String key, boolean defaultValue) {
        Boolean result = getPropertyToBoolean(key);
        return result != null ? result : defaultValue;
    }

    /**
     * 取得系统的运行模式
     *
     * @return 运行模式
     */
    private static ApplicationMode getApplicationModel() {
        final String mode = getProperty(GojaPropConst.APPMODE, "dev").toUpperCase();
        return ApplicationMode.valueOf(mode);
    }



    public static boolean containsKey(String key) {
        return configProps.containsKey(key);
    }

    public static Object get(String key) {
        return configProps.get(key);
    }

    public static boolean isApi() {
        return api;
    }
}
