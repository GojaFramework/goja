/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.wall.WallFilter;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.Const;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.AnsiSqlDialect;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.activerecord.dialect.PostgreSqlDialect;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.activerecord.dialect.Sqlite3Dialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import com.jfinal.plugin.druid.IDruidStatViewAuth;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.render.FreeMarkerRender;
import com.jfinal.render.ViewType;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import freemarker.template.Configuration;
import goja.annotation.HandlerBind;
import goja.annotation.PluginBind;
import goja.cache.Cache;
import goja.cache.EhCacheImpl;
import goja.app.GojaConfig;
import goja.exceptions.DatabaseException;
import goja.initialize.ctxbox.ClassBox;
import goja.initialize.ctxbox.ClassType;
import goja.job.JobsPlugin;
import goja.logging.Logger;
import goja.logging.LoggerInit;
import goja.mvc.AppLoadEvent;
import goja.mvc.auto.AutoBindRoutes;
import goja.mvc.auto.AutoOnLoadInterceptor;
import goja.mvc.error.GojaErrorRenderFactory;
import goja.mvc.render.ftl.PrettyTimeDirective;
import goja.mvc.render.ftl.layout.BlockDirective;
import goja.mvc.render.ftl.layout.ExtendsDirective;
import goja.mvc.render.ftl.layout.OverrideDirective;
import goja.mvc.render.ftl.layout.SuperDirective;
import goja.mvc.render.ftl.shiro.ShiroTags;
import goja.plugins.index.IndexPlugin;
import goja.plugins.monogo.MongoPlugin;
import goja.plugins.quartz.QuartzPlugin;
import goja.plugins.shiro.ShiroPlugin;
import goja.plugins.sqlinxml.SqlInXmlPlugin;
import goja.plugins.sqlmap.SqlMapPlugin;
import goja.plugins.tablebind.AutoTableBindPlugin;
import goja.rapid.syslog.LogProcessor;
import goja.rapid.syslog.SysLogInterceptor;
import goja.rapid.upload.OreillyCosExt;
import goja.security.shiro.SecurityUserData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Protocol;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * <p> The core of goja. </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-06-02 11:11
 * @since JDK 1.6
 */
public class Goja extends JFinalConfig {

    public static final String FTL_HTML_PREFIX = ".ftl";

    private static final org.slf4j.Logger logger         = LoggerFactory.getLogger(Goja.class);
    private static final String           DEFAULT_DOMAIN = "http://127.0.0.1:8080/";
    public static        boolean          initlization   = false;
    public static        boolean          started        = false;
    // the application configuration.
    public static Properties       configuration;
    // the application view path.
    public static String           viewPath;
    public static String           domain;
    public static String           appName;
    public static String           appVersion;
    public static SecurityUserData securityUserData;
    public static File applicationPath = null;
    private Routes _routes;

    /**
     * 为方便测试用例的使用，这个提供一个手动初始化的方法为测试用例使用,调用采用反射机制
     * <p/>
     * Reflect.on(Goja.class).call("initWithTest");
     */
    static void initWithTest() {

        // set config propertis.
        configuration = GojaConfig.getConfigProps();
        initlization = true;

        LoggerInit.init();
    }

    @Override
    public void configConstant(Constants constants) {
        // set config propertis.
        configuration = GojaConfig.getConfigProps();
        // init application path
        applicationPath = new File(PathKit.getWebRootPath());

        initlization = true;

        // dev_mode
        constants.setDevMode( GojaConfig.applicationMode().isDev());
        // fixed: render view has views//xxx.ftl
        viewPath = GojaConfig.getProperty("app.viewpath", File.separator + "WEB-INF" + File.separator + "views");
        constants.setBaseViewPath(viewPath);

        appName = GojaConfig.appName();
        appVersion = GojaConfig.appVersion();

        // init wxchat config
        final String wx_url = GojaConfig.getProperty("wx.url");
        if (!Strings.isNullOrEmpty(wx_url)) {
            // Config Wx Api
            ApiConfigKit.setDevMode( GojaConfig.applicationMode().isDev());
        }

        if (GojaConfig.enable_security()) {
            final List<Class> security_user = ClassBox.getInstance().getClasses(ClassType.SECURITY_DATA);
            if (security_user != null && security_user.size() == 1) {
                try {
                    securityUserData = (SecurityUserData) security_user.get(0).newInstance();
                } catch (InstantiationException e) {
                    logger.error("the security user data has error!", e);
                } catch (IllegalAccessException e) {
                    logger.error("the security user data has error!", e);
                }
            }
        }

        domain = GojaConfig.getProperty("domain", DEFAULT_DOMAIN + appName);
        String view_type = GojaConfig.getProperty("app.viewtype");
        if (!StrKit.isBlank(view_type)) {
            setViewType(constants, view_type);
        } else {
            constants.setFreeMarkerViewExtension(FTL_HTML_PREFIX);
            setFtlSharedVariable();
        }
        constants.setErrorRenderFactory(new GojaErrorRenderFactory());

        constants.setMaxPostSize(GojaConfig.getPropertyToInt("app.maxfilesize", Const.DEFAULT_MAX_POST_SIZE));
        OreillyCosExt.init(constants.getUploadedFileSaveDirectory(),
                           constants.getMaxPostSize(), constants.getEncoding());
    }

    @Override
    public void configRoute(Routes routes) {
        this._routes = routes;
        routes.add(new AutoBindRoutes());
    }

    @Override
    public void configPlugin(Plugins plugins) {
        // fixed: https://github.com/GojaFramework/goja/issues/4
        started = true;

        if (new File(PathKit.getRootClassPath() + File.separator + "ehcache.xml").exists()) {
            plugins.add(new EhCachePlugin());
        } else {
            plugins.add(new EhCachePlugin(EhCacheImpl.getInstance().getCacheManager()));
        }


        initDataSource(plugins);

        if (GojaConfig.enable_security()) {
            plugins.add(new ShiroPlugin(this._routes));
        }

        if (GojaConfig.getPropertyToBoolean("job", false)) {
            plugins.add(new QuartzPlugin());
        }


        final String index_path = GojaConfig.getProperty("index.path");
        if (!Strings.isNullOrEmpty(index_path)) {
            plugins.add(new IndexPlugin(index_path));
        }

        final String mongo_host = GojaConfig.getProperty("mongo.host", StringUtils.EMPTY);
        final String mongo_url = GojaConfig.getProperty("mongo.url", StringUtils.EMPTY);
        if (!Strings.isNullOrEmpty(mongo_host) || !Strings.isNullOrEmpty(mongo_url)) {
            int mongo_port = GojaConfig.getPropertyToInt("mongo.port", MongoPlugin.DEFAUL_PORT);
            String mongo_db = GojaConfig.getProperty("mongo.db", "test");
            boolean moriph = GojaConfig.getPropertyToBoolean("mongo.moriph", false);
            String pkgs = GojaConfig.getProperty("mongo.moriph.entitys", MongoPlugin.DEFAULT_PKGS);
            final MongoPlugin mongodb = new MongoPlugin(mongo_host, mongo_port, mongo_db, moriph, pkgs);
            plugins.add(mongodb);
        }

        final String redis_host = GojaConfig.getProperty("redis.host", StringUtils.EMPTY);
        if (!Strings.isNullOrEmpty(redis_host)) {
            final Map<String, Properties> redisConfig = GojaConfig.loadRedisConfig(GojaConfig.getConfigProps());
            for (String redisCacheName : redisConfig.keySet()) {
                Properties properties = redisConfig.get(redisCacheName);
                final String strProt = properties.getProperty("redis.port");
                int port;
                port = Strings.isNullOrEmpty(strProt) ? Protocol.DEFAULT_PORT : Ints.tryParse(strProt);
                final RedisPlugin jedis = new RedisPlugin(redisCacheName, redis_host, port);
                plugins.add(jedis);
            }
        }

        final List<Class> plugins_clses = ClassBox.getInstance().getClasses(ClassType.PLUGIN);
        if (plugins_clses != null && !plugins_clses.isEmpty()) {
            PluginBind pluginBind;
            for (Class plugin : plugins_clses) {
                pluginBind = (PluginBind) plugin.getAnnotation(PluginBind.class);
                if (pluginBind != null) {
                    try {
                        plugins.add((com.jfinal.plugin.IPlugin) plugin.newInstance());
                    } catch (InstantiationException e) {
                        Logger.error("The plugin instance is error!", e);
                    } catch (IllegalAccessException e) {
                        Logger.error("The plugin instance is error!", e);
                    }
                }
            }
        }

        // Because the system itself the task of startup tasks, so the system task plugin must be the last to join the list
        plugins.add(new JobsPlugin());

    }

    @Override
    public void configInterceptor(Interceptors interceptors) {
        try {
            final List<Class> log_precess = ClassBox.getInstance().getClasses(ClassType.LOGPERCESSOR);
            if (log_precess != null && !log_precess.isEmpty()) {
                Class log_percess_impl_cls = log_precess.get(0);
                URL config_url = com.google.common.io.Resources.getResource("syslog.json");
                if (config_url != null) {
                    SysLogInterceptor sysLogInterceptor = new SysLogInterceptor();
                    sysLogInterceptor = sysLogInterceptor.setLogProcesser((LogProcessor) log_percess_impl_cls.newInstance(), config_url.getPath());
                    if (sysLogInterceptor != null) {
                        interceptors.add(sysLogInterceptor);
                    }
                }
            }

        } catch (IllegalArgumentException e) {
            logger.error("Enable the system operation log interceptor abnormalities.", e);
        } catch (InstantiationException e) {
            logger.error("Enable the system operation log interceptor abnormalities.", e);
        } catch (IllegalAccessException e) {
            logger.error("Enable the system operation log interceptor abnormalities.", e);
        }

        new AutoOnLoadInterceptor(interceptors).load();
    }

    @Override
    public void configHandler(Handlers handlers) {

        handlers.add(new ContextPathHandler("ctx"));
        final boolean monitorDB = GojaConfig.getPropertyToBoolean("db.monitor", false);
        if(monitorDB){
            final String view_url = GojaConfig.getProperty("db.monitor.url", "/druid/monitor");

            final DruidStatViewHandler dvh = new DruidStatViewHandler(view_url, new IDruidStatViewAuth() {
                @Override
                public boolean isPermitted(HttpServletRequest request) {
                    HttpSession hs = request.getSession(false);
                    return (hs != null);
                }
            });


            handlers.add(dvh);
        }




        final List<Class> handler_clses = ClassBox.getInstance().getClasses(ClassType.HANDLER);
        if (handler_clses != null && !handler_clses.isEmpty()) {
            HandlerBind handlerBind;
            for (Class handler : handler_clses) {
                handlerBind = (HandlerBind) handler.getAnnotation(HandlerBind.class);
                if (handlerBind != null) {
                    try {
                        handlers.add((com.jfinal.handler.Handler) handler.newInstance());
                    } catch (InstantiationException e) {
                        logger.error("The Handler instance is error!", e);
                    } catch (IllegalAccessException e) {
                        logger.error("The Handler instance is error!", e);
                    }
                }
            }
        }
    }

    @Override
    public void afterJFinalStart() {
        List<Class> appCliasses = ClassBox.getInstance().getClasses(ClassType.APP);
        if (appCliasses != null && !appCliasses.isEmpty()) {
            for (Class appCliass : appCliasses) {

                AppLoadEvent event;
                try {
                    event = (AppLoadEvent) appCliass.newInstance();
                    if (event != null) {
                        event.load();
                    }
                } catch (Throwable t) {
                    logger.error("load event is error!", t);
                }
            }
        }
//        GojaConfig.clear();
    }

    @Override
    public void beforeJFinalStop() {
        ClassBox.getInstance().clearBox();
        Cache.stop();
        started = false;
    }

    /**
     * init databases.
     *
     * @param plugins plugin.
     */
    private void initDataSource(final Plugins plugins) {

        final Map<String, Properties> dbConfig = GojaConfig.loadDBConfig(GojaConfig.getConfigProps());
        for (String db_config : dbConfig.keySet()) {
            final Properties db_props = dbConfig.get(db_config);
            if (db_props != null && !db_props.isEmpty()) {
                configDatabasePlugins(db_config, plugins,
                                      db_props.getProperty("db.url"),
                                      db_props.getProperty("db.username"),
                                      db_props.getProperty("db.password"));
            }
        }

        if (GojaConfig.getPropertyToBoolean("db.sqlinxml", true)) {
            plugins.add(new SqlInXmlPlugin());
        }
        if (GojaConfig.getPropertyToBoolean("db.sqlmap", true)) {
            plugins.add(new SqlMapPlugin());
        }

    }

    /**
     * The configuration database, specify the name of the database.
     *
     * @param configName the database config name.
     * @param plugins    the jfinal plugins.
     * @param db_url     the database connection url.
     * @param username   the login username.
     * @param password   the login password.
     */
    private void configDatabasePlugins(String configName, final Plugins plugins, String db_url, String username, String password) {
        if (!Strings.isNullOrEmpty(db_url)) {
            String dbtype = JdbcUtils.getDbType(db_url, StringUtils.EMPTY);
            String driverClassName;
            try {
                driverClassName = JdbcUtils.getDriverClassName(db_url);
            } catch (SQLException e) {
                throw new DatabaseException(e.getMessage(), e);
            }
            final DruidPlugin druidPlugin = new DruidPlugin(db_url, username, password, driverClassName);

            // set validator
            if (!StringUtils.equals(JdbcConstants.MYSQL, dbtype)) {
                if (StringUtils.equals(JdbcConstants.ORACLE, dbtype)) {
                    druidPlugin.setValidationQuery("SELECT 1 FROM dual");
                } else if (StringUtils.equals(JdbcConstants.HSQL, dbtype)) {
                    druidPlugin.setValidationQuery("SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS");
                } else if (StringUtils.equals(JdbcConstants.DB2, dbtype)) {
                    druidPlugin.setValidationQuery("SELECT 1 FROM sysibm.sysdummy1");
                } else {
                    druidPlugin.setValidationQuery("SELECT 1 ");
                }
            }
            druidPlugin.addFilter(new StatFilter());

            final String initialSize = GojaConfig.getProperty("db.initial.size");
            if (!Strings.isNullOrEmpty(initialSize)) {
                druidPlugin.setInitialSize(Ints.tryParse(initialSize));
            }
            final String initial_minidle = GojaConfig.getProperty("db.initial.minidle");
            if (!Strings.isNullOrEmpty(initial_minidle)) {
                druidPlugin.setMinIdle(Ints.tryParse(initial_minidle));
            }

            final String initial_maxwait = GojaConfig.getProperty("db.initial.maxwait");
            if (!Strings.isNullOrEmpty(initial_maxwait)) {
                druidPlugin.setMaxWait(Ints.tryParse(initial_maxwait));
            }
            final String initial_active = GojaConfig.getProperty("db.initial.active");
            if (!Strings.isNullOrEmpty(initial_active)) {
                druidPlugin.setMaxActive(Ints.tryParse(initial_active));
            }
            final String timeBetweenEvictionRunsMillis = GojaConfig.getProperty("db.timeBetweenEvictionRunsMillis");
            if (!Strings.isNullOrEmpty(timeBetweenEvictionRunsMillis)) {
                druidPlugin.setTimeBetweenEvictionRunsMillis(Ints.tryParse(timeBetweenEvictionRunsMillis));
            }
            final String minEvictableIdleTimeMillis = GojaConfig.getProperty("db.minEvictableIdleTimeMillis");
            if (!Strings.isNullOrEmpty(minEvictableIdleTimeMillis)) {
                druidPlugin.setMinEvictableIdleTimeMillis(Ints.tryParse(minEvictableIdleTimeMillis));
            }


            final WallFilter wall = new WallFilter();
            wall.setDbType(dbtype);
            druidPlugin.addFilter(wall);
            // 增加 LogFilter 输出JDBC执行的日志
            druidPlugin.addFilter(new Slf4jLogFilter());
            plugins.add(druidPlugin);

            //  setting db table name like 'dev_info'
            final AutoTableBindPlugin atbp = new AutoTableBindPlugin(configName, druidPlugin);

            if (!StringUtils.equals(dbtype, JdbcConstants.MYSQL)) {
                if (StringUtils.equals(dbtype, JdbcConstants.ORACLE)) {
                    atbp.setDialect(new OracleDialect());
                    atbp.setContainerFactory(new CaseInsensitiveContainerFactory(true));
                } else if (StringUtils.equals(dbtype, JdbcConstants.POSTGRESQL)) {
                    atbp.setDialect(new PostgreSqlDialect());
                    atbp.setContainerFactory(new CaseInsensitiveContainerFactory(true));
                } else if (StringUtils.equals(dbtype, JdbcConstants.H2)) {
                    atbp.setDialect(new AnsiSqlDialect());
                    atbp.setContainerFactory(new CaseInsensitiveContainerFactory(true));
                } else if (StringUtils.equals(dbtype, "sqlite")) {
                    atbp.setDialect(new Sqlite3Dialect());
                } else if (StringUtils.equals(dbtype, JdbcConstants.JTDS)) {
                    atbp.setDialect(new SqlServerDialect());
                } else {
                    System.err.println("database type is use mysql.");
                }
            }
            atbp.setShowSql(GojaConfig.applicationMode().isDev());
            plugins.add(atbp);

        }
    }

    /**
     * set view type.
     *
     * @param constants jfinal constant.
     * @param view_type view type.
     */
    private void setViewType(Constants constants, String view_type) {
        final ViewType viewType = ViewType.valueOf(view_type.toUpperCase());
        if (viewType == ViewType.FREE_MARKER) {
            constants.setFreeMarkerViewExtension(FTL_HTML_PREFIX);
            setFtlSharedVariable();
        }
        constants.setViewType(viewType);
    }

    /**
     * set freemarker variable.
     */
    private void setFtlSharedVariable() {
        // custmer variable
        final Configuration config = FreeMarkerRender.getConfiguration();
        config.setSharedVariable("block", new BlockDirective());
        config.setSharedVariable("extends", new ExtendsDirective());
        config.setSharedVariable("override", new OverrideDirective());
        config.setSharedVariable("super", new SuperDirective());
        // 增加日期美化指令（类似 几分钟前）
        config.setSharedVariable("prettytime", new PrettyTimeDirective());
        if (GojaConfig.enable_security()) {
            config.setSharedVariable("shiro", new ShiroTags(config.getObjectWrapper()));
        }
    }

}
