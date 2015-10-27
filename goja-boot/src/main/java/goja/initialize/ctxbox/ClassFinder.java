/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.initialize.ctxbox;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.Controller;
import com.jfinal.handler.Handler;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.Model;
import goja.core.StringPool;
import goja.core.app.GojaConfig;
import goja.job.Job;
import goja.mvc.AppLoadEvent;
import goja.rapid.syslog.LogProcessor;
import goja.security.shiro.SecurityUserData;

import java.util.List;

/**
 * <p>
 * Class Finder.
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-01-19 22:32
 * @since JDK 1.6
 */
public class ClassFinder {


    /**
     * find class files.
     */
    public static void find() {

        ClassSearcher searcher = ClassSearcher.of(Model.class, Controller.class, Interceptor.class,
                Job.class, org.quartz.Job.class, AppLoadEvent.class, IPlugin.class, Handler.class,
                LogProcessor.class, SecurityUserData.class)
                .inJars(GojaConfig.getAppJars());
        List<Class<?>> classFileList = searcher.search();
        for (Class cls : classFileList) {
            ClassBox.getInstance().push(cls);
        }
    }

    /**
     * find class files.
     */
    public static void findWithTest() {
        find();

        String testRoolClassPath = PathKit.getRootClassPath();
        String test_classpath = testRoolClassPath.replace("test-", StringPool.EMPTY);
        ClassSearcher test_searcher = ClassSearcher.of(Model.class, Controller.class, Interceptor.class,
                Job.class, org.quartz.Job.class, AppLoadEvent.class, IPlugin.class, Handler.class,
                LogProcessor.class, SecurityUserData.class).classpath(test_classpath)
                .inJars(GojaConfig.getAppJars());

        for (Class cls : test_searcher.search()) {
            ClassBox.getInstance().push(cls);
        }
    }


}
