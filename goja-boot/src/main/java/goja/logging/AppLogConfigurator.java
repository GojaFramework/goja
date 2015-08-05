/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.logging;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.StatusManager;
import com.google.common.base.Charsets;
import goja.Goja;
import goja.GojaConfig;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * .
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-06-02 21:13
 * @since JDK 1.6
 */
public class AppLogConfigurator {


    private AppLogConfigurator() {
    }

    public static void configure(final LoggerContext lc) {
        StatusManager sm = lc.getStatusManager();
        if (sm != null) {
            sm.add(new InfoStatus("Setting up default configuration.", lc));
        }
        ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<ILoggingEvent>();
        ca.setContext(lc);
        ca.setName("console");

        final RollingFileAppender rfa = new RollingFileAppender();

        final String logger_file = GojaConfig.getProperty("logger.path", "/logs/" + (Goja.mode.isTest() ? GojaConfig.appName() + "-test" : GojaConfig.appName()) + ".log");
        rfa.setFile(logger_file);

        final TimeBasedRollingPolicy rollingPolicy = new TimeBasedRollingPolicy();
        rollingPolicy.setParent(rfa);
        rollingPolicy.setMaxHistory(15);
        rollingPolicy.setFileNamePattern(StringUtils.replace(logger_file, ".log", ".%d{yyyy-MM-dd}.%i.log"));
        SizeAndTimeBasedFNATP timeBasedTriggering = new SizeAndTimeBasedFNATP();
        timeBasedTriggering.setMaxFileSize("100MB");
        rollingPolicy.setTimeBasedFileNamingAndTriggeringPolicy(timeBasedTriggering);
        rollingPolicy.setContext(lc);
        rfa.setRollingPolicy(rollingPolicy);
        rfa.setContext(lc);
        rfa.setName("app_log_file");

        PatternLayoutEncoder pl = new PatternLayoutEncoder();
        pl.setContext(lc);
        pl.setCharset(Charsets.UTF_8);
        pl.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        pl.start();

        ca.setEncoder(pl);
        ca.start();

        rfa.setEncoder(pl);
        rfa.start();

        // init async loggin
        AsyncAppender asyncAppender = new AsyncAppender();
        asyncAppender.setContext(lc);
        asyncAppender.addAppender(rfa);
        asyncAppender.setQueueSize(512);
        asyncAppender.setDiscardingThreshold(0);

        final Level config_level = Level.toLevel(GojaConfig.getProperty("logger"), Level.INFO);
        final boolean mode = GojaConfig.isDev();
        Logger rootLogger = lc.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(config_level);
        rootLogger.addAppender(ca);
        rootLogger.addAppender(asyncAppender);

        Logger appLogger = lc.getLogger("app");
        appLogger.setLevel(mode ? Level.DEBUG : config_level);
        appLogger.addAppender(ca);
        appLogger.addAppender(asyncAppender);

        if (mode) {
            Logger gojaLogger = lc.getLogger("goja");
            gojaLogger.setLevel(Level.DEBUG);
            gojaLogger.addAppender(ca);
            Logger jfinalLogger = lc.getLogger("com.jfinal");
            jfinalLogger.setLevel(Level.DEBUG);
            jfinalLogger.addAppender(ca);
        }
    }

}
