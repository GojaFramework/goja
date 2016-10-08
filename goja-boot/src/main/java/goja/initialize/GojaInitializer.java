/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.initialize;

import goja.core.app.GojaConfig;
import goja.core.kits.StopWatch;
import goja.initialize.ansi.AnsiColor;
import goja.initialize.ansi.AnsiOutput;
import goja.initialize.ansi.AnsiStyle;
import goja.initialize.ctxbox.ClassFinder;
import goja.logging.LoggerInit;
import com.jfinal.kit.PathKit;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Ordering;

import com.alibaba.druid.util.JdbcUtils;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SQLExec;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * <p> 通过Servlet 3.0 的动态加载方式加载JFinal，免去Web.xml的配置. </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-01-02 15:06
 * @since JDK 1.6
 */
public class GojaInitializer implements ServletContainerInitializer {

    private static final Logger logger = LoggerFactory.getLogger(GojaInitializer.class);


    private static final String BANNER = "\n" +
            "        ___           ___         ___          ___     \n" +
            "       /  /\\         /  /\\       /  /\\        /  /\\    \n" +
            "      /  /:/_       /  /::\\     /  /:/       /  /::\\   \n" +
            "     /  /:/ /\\     /  /:/\\:\\   /__/::\\      /  /:/\\:\\  \n" +
            "    /  /:/_/::\\   /  /:/  \\:\\  \\__\\/\\:\\    /  /:/~/::\\ \n" +
            "   /__/:/__\\/\\:\\ /__/:/ \\__\\:\\    \\  \\:\\  /__/:/ /:/\\:\\\n" +
            "   \\  \\:\\ /~~/:/ \\  \\:\\ /  /:/     \\__\\:\\ \\  \\:\\/:/__\\/\n" +
            "    \\  \\:\\  /:/   \\  \\:\\  /:/      /  /:/  \\  \\::/     \n" +
            "     \\  \\:\\/:/     \\  \\:\\/:/      /__/:/    \\  \\:\\     \n" +
            "      \\  \\::/       \\  \\::/       \\__\\/      \\  \\:\\    \n" +
            "       \\__\\/         \\__\\/                    \\__\\/    \n" +
            "";


    private static final String GOJA_BOOT = " :: Goja Boot :: ";


    private static final int STRAP_LINE_SIZE = 42;

    private static StopWatch _stopWatch = new StopWatch();

    @Override
    public void onStartup(Set<Class<?>> classSet, ServletContext ctx)
            throws ServletException {

        System.out.println(BANNER);

        String version = GojaBootVersion.getVersion();
        version = (version == null ? "" : " (v" + version + ")");
        String padding = "";
        while (padding.length() < STRAP_LINE_SIZE
                - (version.length() + GOJA_BOOT.length())) {
            padding += " ";
        }
        System.out.println(AnsiOutput.toString(AnsiColor.GREEN, GOJA_BOOT,
                AnsiColor.DEFAULT, padding, AnsiStyle.FAINT, version));

        _stopWatch.start();

        ImageIO.setUseCache(false);
        // 初始化配置文件
        GojaConfig.init();
        // 日志处理
        LoggerInit.init();

        if (GojaConfig.isSecurity()) {
            File shiroIniFile = new File(PathKit.getRootClassPath() + File.separator + "shiro.ini");
            if (shiroIniFile.exists()) {
                ctx.addListener("org.apache.shiro.web.env.EnvironmentLoaderListener");
                ctx.addFilter("Goja@shiroFilter", "org.apache.shiro.web.servlet.ShiroFilter")
                        .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
            } else {
                ctx.addFilter("Goja@shiroFilter", "goja.security.shiro.GojaShiroFilter")
                        .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
            }
        }
        //logger context destroy listener.
        ctx.addListener("ch.qos.logback.classic.selector.servlet.ContextDetachingSCL");

        //Before starting JFinal, lookup class file on the classpath.
        ClassFinder.find();


        FilterRegistration.Dynamic jfinalFilter =
                ctx.addFilter("goja&jfinal", "com.jfinal.core.JFinalFilter");

        jfinalFilter.setInitParameter("configClass", "goja.Goja");
        jfinalFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        // 支持异步请求处理
        jfinalFilter.setAsyncSupported(true);

        if (GojaConfig.getApplicationMode().isDev()) {
            runScriptInitDb();
        }
    }

    public static void finish(){
        logger.info(getStartedMessage(_stopWatch).toString());
    }

    private static StringBuilder getStartedMessage(StopWatch stopWatch) {
        StringBuilder message = new StringBuilder();
        message.append("Started ");
        message.append(GojaConfig.getAppName());
        message.append(" in ");
        message.append(stopWatch.getTotalTimeSeconds());
        try {
            double uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0;
            message.append(" seconds (JVM running for ").append(uptime).append(")");
        } catch (Throwable ex) {
            // No JVM time available
        }
        return message;
    }

    private void runScriptInitDb() {
        try {

            String script_path = GojaConfig.getProperty("db.script.path", "misc/sql/");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(script_path)
                    , "The Database init database script init!");
            final String real_script_path = PathKit.getRootClassPath() + File.separator + script_path;
            final File script_dir = new File(real_script_path);
            if (script_dir.exists() && script_dir.isDirectory()) {
                final String db_url = GojaConfig.getDefaultDBUrl();
                Preconditions.checkNotNull(db_url, "The DataBase connection url is must!");

                if (logger.isDebugEnabled()) {
                    logger.debug("init db script with {}", real_script_path);
                }

                Collection<File> list_script_files
                        = Ordering.natural()
                        .sortedCopy(FileUtils.listFiles(script_dir, new String[]{"sql"}, false));
                for (File list_script_file : list_script_files) {
                    logger.debug("run db script file is {}", list_script_file);
                    final SQLExec sql_exec = new SQLExec();
                    final String driverClassName = JdbcUtils.getDriverClassName(db_url);
                    sql_exec.setDriver(driverClassName);
                    sql_exec.setUrl(db_url);
                    final String db_username = GojaConfig.getDefaultDBUsername();
                    final String db_password = GojaConfig.getDefaultDBPassword();
                    sql_exec.setUserid(db_username);
                    sql_exec.setPassword(db_password);

                    sql_exec.setOnerror(
                            (SQLExec.OnError) (EnumeratedAttribute.getInstance(SQLExec.OnError.class, "abort")));
                    sql_exec.setPrint(true);
                    sql_exec.setProject(new Project());
                    sql_exec.setSrc(list_script_file);
                    try {
                        sql_exec.execute();
                    } catch (Exception e) {
                        logger.error("the init database has already ok!, ", e);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("init db script is error!", e);
            throw Throwables.propagate(e);
        }
    }
}
