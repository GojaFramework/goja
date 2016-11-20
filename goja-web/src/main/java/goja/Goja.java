/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja;

import goja.core.Func;
import goja.core.app.ApplicationMode;
import goja.core.app.GojaConfig;
import goja.core.app.GojaPropConst;
import goja.core.exceptions.GojaException;
import goja.core.exceptions.UnexpectedException;
import goja.core.kits.reflect.ClassPathScanning;
import goja.core.sqlinxml.SqlInXmlPlugin;
import goja.initialize.DruidDbIntializer;
import goja.initialize.GojaInitializer;
import goja.job.Job;
import goja.job.JobsPlugin;
import goja.logging.Logger;
import goja.logging.LoggerInit;
import goja.mvc.PageViewKit;
import goja.mvc.controller.ControllerBindRoutes;
import goja.mvc.interceptor.AutoInterceptor;
import goja.mvc.error.GojaErrorRenderFactory;
import goja.mvc.render.ftl.PrettyTimeDirective;
import goja.mvc.render.ftl.layout.BlockDirective;
import goja.mvc.render.ftl.layout.ExtendsDirective;
import goja.mvc.render.ftl.layout.OverrideDirective;
import goja.mvc.render.ftl.layout.SuperDirective;
import goja.mvc.render.ftl.shiro.ShiroTags;
import goja.plugins.shiro.ShiroInterceptor;
import goja.plugins.shiro.ShiroPlugin;
import goja.rapid.job.QuartzPlugin;
import goja.rapid.mongo.MongoPlugin;
import goja.rapid.mvc.handler.CutSessionIdHandler;
import goja.rapid.mvc.interceptor.syslog.LogProcessor;
import goja.rapid.mvc.interceptor.syslog.SysLogInterceptor;
import goja.rapid.mvc.upload.filerenamepolicy.DateRandomFileRenamePolicy;
import goja.rapid.mvc.upload.filerenamepolicy.RandomFileRenamePolicy;
import goja.security.shiro.SecurityUserData;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.Const;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.json.FastJsonFactory;
import com.jfinal.json.JacksonFactory;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import com.jfinal.plugin.druid.IDruidStatViewAuth;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.render.FreeMarkerRender;
import com.jfinal.render.ViewType;
import com.jfinal.upload.OreillyCos;
import com.jfinal.weixin.sdk.api.ApiConfigKit;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.google.common.primitives.Ints;

import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import freemarker.template.Configuration;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import redis.clients.jedis.Protocol;

//import goja.annotation.HandlerBind;

/**
 * <p> The core of goja. </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-06-02 11:11
 * @since JDK 1.6
 */
public class Goja extends JFinalConfig {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Goja.class);
    // the application configuration.
    public static Properties configuration;
    // the application view path.
    public static String     viewPath;
    public static String     appName;


    public static SecurityUserData securityUserData;

    static boolean started = false;

    private static JobsPlugin jobsPlugin = new JobsPlugin();

    private Routes _routes;


    /**
     * 为方便测试用例的使用，这个提供一个手动初始化的方法为测试用例使用,调用采用反射机制 <p/> Reflect.on(Goja.class).call("initWithTest");
     */
    @SuppressWarnings("unused")
    static void initWithTest() {

        // set config propertis.
        configuration = GojaConfig.getConfigProps();

        LoggerInit.init();
    }

    @Override
    public void configConstant(Constants constants) {
        // set config propertis.
        configuration = GojaConfig.getConfigProps();

        // dev_mode
        final ApplicationMode applicationMode = GojaConfig.getApplicationMode();
        final boolean         isDev           = applicationMode.isDev();
        constants.setDevMode(isDev);
        // fixed: render view has views//xxx.ftl
        final String DEFAULT_VIEW_PATH = PageViewKit.WEBINF_DIR + "views";
        viewPath = GojaConfig.getProperty(GojaPropConst.APP_VIEWPATH, DEFAULT_VIEW_PATH);
        constants.setBaseViewPath(viewPath);
        if (applicationMode.isProd()) {
            // 404由于在开发模式下的提示信息，由于404触发的，所以在开发模式不启动404视图界面
            constants.setError404View(PageViewKit.get404PageView());
        }
        constants.setError500View(PageViewKit.get500PageView());
        constants.setError403View(PageViewKit.get403PageView());


        appName = GojaConfig.getAppName();

        // init wxchat config
        final String wx_url = GojaConfig.getProperty(GojaPropConst.APP_WXCHAT_URL);
        if (!Strings.isNullOrEmpty(wx_url)) {
            // Config Wx Api
            ApiConfigKit.setDevMode(isDev);
        }

        if (GojaConfig.isSecurity()) {
            final Set<Class<? extends SecurityUserData>> securityUserClass = ClassPathScanning.scan(SecurityUserData.class);
            if (CollectionUtils.isNotEmpty(securityUserClass)) {
                for (Class<? extends SecurityUserData> securityUserClas : securityUserClass) {
                    try {
                        securityUserData = securityUserClas.newInstance();
                        break;
                    } catch (InstantiationException | IllegalAccessException e) {
                        logger.error("the security user data has error!", e);
                    }
                }
            }
        }

        final boolean jspViewType = GojaConfig.getPropertyToBoolean(GojaPropConst.APP_VIEW_JSP, false);
        if (jspViewType) {
            constants.setViewType(ViewType.JSP);
        } else {
            constants.setFreeMarkerViewExtension(".ftl");
            setFtlSharedVariable();
        }
        if (isDev) {
            constants.setErrorRenderFactory(new GojaErrorRenderFactory());
        }
        final int uploadMaxFileSize = GojaConfig.getPropertyToInt(GojaPropConst.APP_UPLOAD_MAXFILESIZE, Const.DEFAULT_MAX_POST_SIZE);
        constants.setMaxPostSize(uploadMaxFileSize);

        final String attachmentPath = GojaConfig.getProperty(GojaPropConst.APP_UPLOAD_PATH, "attachment");
        constants.setBaseUploadPath(attachmentPath);
        constants.setBaseDownloadPath(attachmentPath);

        final String jsonMode = GojaConfig.getJsonMode();
        if (!Strings.isNullOrEmpty(jsonMode)) {
            if (StringUtils.equalsIgnoreCase("fastjson", jsonMode)) {
                constants.setJsonFactory(new FastJsonFactory());
            } else if (StringUtils.equalsIgnoreCase("jackson", jsonMode)) {
                constants.setJsonFactory(new JacksonFactory());
            }
        }

        final String fileRenamePolicy = GojaConfig.getProperty(GojaPropConst.APP_UPLOAD_FILERENAMEPOLICY, "default");
        if (StringUtils.equalsIgnoreCase(fileRenamePolicy, "date")) {
            OreillyCos.setFileRenamePolicy(new DateRandomFileRenamePolicy());
        } else if (StringUtils.equalsIgnoreCase(fileRenamePolicy, "radom")) {
            OreillyCos.setFileRenamePolicy(new RandomFileRenamePolicy());
        } else if (StringUtils.equalsIgnoreCase(fileRenamePolicy, "default")) {
            OreillyCos.setFileRenamePolicy(new DefaultFileRenamePolicy());
        } else {
            logger.warn("Upload folder naming of the unknown!");
        }
    }

    @Override
    public void configRoute(Routes routes) {
        this._routes = routes;
        routes.add(new ControllerBindRoutes());
    }

    @Override
    public void configPlugin(Plugins plugins) {
        plugins.add(jobsPlugin);
        // fixed: https://github.com/GojaFramework/goja/issues/4
        started = true;

        initDataSource(plugins);

        // 判断初始化缓存信息
        plugins.add(new EhCachePlugin());


        if (GojaConfig.isSecurity()) {
            plugins.add(new ShiroPlugin(this._routes));
        }

        if (GojaConfig.getPropertyToBoolean(GojaPropConst.APPJOB, false)) {
            plugins.add(new QuartzPlugin());
        }

        final boolean mongoFlag = GojaConfig.getPropertyToBoolean(GojaPropConst.MONGO, false);
        if (mongoFlag) {
            logger.info("开始初始化MongoDB插件");

            final String mongoHost     = GojaConfig.getProperty(GojaPropConst.MONGO_HOST);
            final String mongoPort     = GojaConfig.getProperty(GojaPropConst.MONGO_PORT);
            final String mongoDatabase = GojaConfig.getProperty(GojaPropConst.MONGO_DB, "test");
            if (Strings.isNullOrEmpty(mongoHost) && Strings.isNullOrEmpty(mongoPort)) {
                plugins.add(new MongoPlugin(mongoDatabase));
            } else {
                plugins.add(new MongoPlugin(mongoHost,
                        MoreObjects.firstNonNull(Ints.tryParse(mongoPort), MongoPlugin.DEFAUL_PORT),
                        mongoDatabase));
            }
        }

        final String redisConfig = GojaConfig.getProperty(GojaPropConst.REDIS_CONFIG);
        if (!Strings.isNullOrEmpty(redisConfig)) {
            final Properties redisConfigProp;
            final File       configFolderFile = GojaConfig.getConfigFolderFile();
            redisConfigProp = configFolderFile == null ? PropKit.use(redisConfig).getProperties()
                    : PropKit.use(FileUtils.getFile(configFolderFile, redisConfig)).getProperties();
            String cacheNames = redisConfigProp.getProperty(GojaPropConst.REDIS_CACHES);
            if (!Strings.isNullOrEmpty(cacheNames)) {
                List<String> cacheNameList = Func.COMMA_SPLITTER.splitToList(cacheNames);
                for (String cacheName : cacheNameList) {
                    final String cacheRedistPort = redisConfigProp.getProperty(cacheName + ".port");
                    final String cacheRedistHost = redisConfigProp.getProperty(cacheName + ".host",
                            String.valueOf(Protocol.DEFAULT_PORT));
                    int port = Strings.isNullOrEmpty(cacheRedistPort) ? Protocol.DEFAULT_PORT
                            : MoreObjects.firstNonNull(Ints.tryParse(cacheRedistPort), Protocol.DEFAULT_PORT);
                    final RedisPlugin jedis = new RedisPlugin(cacheName, cacheRedistHost, port);
                    plugins.add(jedis);
                }
            }
        } else {
            final String redis_host = GojaConfig.getProperty(GojaPropConst.REDIS_HOST, StringUtils.EMPTY);
            if (!Strings.isNullOrEmpty(redis_host)) {
                final String cacheName =
                        GojaConfig.getProperty(GojaPropConst.REDIS_CACHENAME, "goja.redis.cache");
                final String strProt = GojaConfig.getProperty(GojaPropConst.REDIS_PORT);
                int port = Strings.isNullOrEmpty(strProt) ? Protocol.DEFAULT_PORT
                        : MoreObjects.firstNonNull(Ints.tryParse(strProt), Protocol.DEFAULT_PORT);
                final RedisPlugin jedis = new RedisPlugin(cacheName, redis_host, port);
                plugins.add(jedis);
            }
        }

        final Set<Class<? extends IPlugin>> pluginClasses = ClassPathScanning.scan(IPlugin.class);
        if (CollectionUtils.isNotEmpty(pluginClasses)) {

            for (Class<? extends IPlugin> pluginClass : pluginClasses) {
                try {
                    plugins.add(pluginClass.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    Logger.error("The plugin instance is error!", e);
                }
            }
        }

    }

    @Override
    public void configInterceptor(Interceptors interceptors) {
        try {

            final Set<Class<? extends LogProcessor>> logProcessors = ClassPathScanning.scan(LogProcessor.class);
            if (CollectionUtils.isNotEmpty(logProcessors)) {
                final Class<? extends LogProcessor> logProcessClass = Lists.newArrayList(logProcessors).get(0);

                final URL config_url = Resources.getResource("syslog.json");
                if (config_url != null) {
                    SysLogInterceptor sysLogInterceptor = new SysLogInterceptor();
                    sysLogInterceptor = sysLogInterceptor.setLogProcesser(logProcessClass.newInstance());
                    if (sysLogInterceptor != null) {
                        interceptors.add(sysLogInterceptor);
                    }
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
            logger.error("Enable the system operation log interceptor abnormalities.", e);
        }

        AutoInterceptor.setAppInterceptor(interceptors);
        if (GojaConfig.isSecurity()) {
            interceptors.add(new ShiroInterceptor());
        }
    }

    @Override
    public void configHandler(Handlers handlers) {

        handlers.add(new ContextPathHandler("ctx"));
        final boolean monitorDB = GojaConfig.getPropertyToBoolean(GojaPropConst.DB_MONITOR, false);
        if (monitorDB) {
            final String view_url =
                    GojaConfig.getProperty(GojaPropConst.DB_MONITOR_URL, "/druid/monitor");

            final DruidStatViewHandler dvh = new DruidStatViewHandler(view_url, new IDruidStatViewAuth() {
                @Override
                public boolean isPermitted(HttpServletRequest request) {
                    HttpSession hs = request.getSession(false);
                    return (hs != null);
                }
            });

            handlers.add(dvh);
        }

        if (GojaConfig.isSecurity()) {
            handlers.add(new CutSessionIdHandler());
        }

        //        final List<Class> handler_clses = ClassBox.getInstance().getClasses(ClassType.HANDLER);
        //        if (handler_clses != null && !handler_clses.isEmpty()) {
        //            HandlerBind handlerBind;
        //            for (Class handler : handler_clses) {
        //                handlerBind = (HandlerBind) handler.getAnnotation(HandlerBind.class);
        //                if (handlerBind != null) {
        //                    try {
        //                        handlers.add((com.jfinal.handler.Handler) handler.newInstance());
        //                    } catch (InstantiationException e) {
        //                        logger.error("The Handler instance is error!", e);
        //                    } catch (IllegalAccessException e) {
        //                        logger.error("The Handler instance is error!", e);
        //                    }
        //                }
        //            }
        //        }
    }

    @Override
    public void afterJFinalStart() {

        final List<Job<?>> applicationStartJobs = jobsPlugin.getApplicationStartJobs();
        if (applicationStartJobs != null && !applicationStartJobs.isEmpty()) {
            for (Job<?> applicationStartJob : applicationStartJobs) {

                applicationStartJob.run();
                if (applicationStartJob.isWasError()) {
                    if (applicationStartJob.getLastException() != null) {
                        logger.error("@OnApplicationStart Job has failed!", applicationStartJob.getLastException());
                    } else {
                        logger.error("@OnApplicationStart Job has failed");
                    }
                }
            }

        }

        GojaInitializer.finish();
    }

    @Override
    public void beforeJFinalStop() {
        started = false;

        final List<Class> stopJobs = jobsPlugin.getApplicationStopJobs();

        if (!(stopJobs == null || stopJobs.isEmpty())) {
            for (Class clazz : stopJobs) {
                try {
                    Job<?> job = ((Job<?>) clazz.newInstance());
                    job.run();

                    if (job.isWasError()) {
                        if (job.getLastException() != null) {
                            logger.error("@OnApplicationStop Job has failed!", job.getLastException());
                        } else {
                            logger.error("@OnApplicationStop Job has failed");
                        }
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new UnexpectedException("ApplicationStop job could not be instantiated", e);
                } catch (Throwable ex) {
                    if (ex instanceof GojaException) {
                        throw (GojaException) ex;
                    }
                    throw new UnexpectedException(ex);
                }
            }
        }
        jobsPlugin.clear();
        jobsPlugin = null;
        _routes.clear();
        _routes = null;

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
                DruidDbIntializer.init(db_config, plugins, db_props);
            }
        }

        if (GojaConfig.getPropertyToBoolean(GojaPropConst.DB_SQLINXML, true)) {
            plugins.add(new SqlInXmlPlugin());
        }
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
        if (GojaConfig.isSecurity()) {
            config.setSharedVariable("shiro", new ShiroTags(config.getObjectWrapper()));
        }
    }
}
