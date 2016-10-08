/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.initialize.ctxbox;

import goja.core.StringPool;
import goja.core.app.GojaConfig;
import goja.core.kits.reflect.Reflect;
import goja.job.Job;
import goja.rapid.mvc.interceptor.syslog.LogProcessor;
import goja.security.shiro.SecurityUserData;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.Model;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * <p> Class Finder. </p>
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
                Job.class, org.quartz.Job.class, IPlugin.class, /*Handler.class,*/
                LogProcessor.class, SecurityUserData.class)
                .scanPackages(Lists.newArrayList(GojaConfig.getAppPackPrefix()))
                .inJars(GojaConfig.getAppJars());
        List<Class<?>> classFileList = searcher.search();
        for (Class cls : classFileList) {
            if (Reflect.isAbstract(cls)) {
                continue;
            }
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
                Job.class, org.quartz.Job.class,  IPlugin.class,/* Handler.class,*/
                LogProcessor.class, SecurityUserData.class).classpath(test_classpath)
                .scanPackages(Lists.newArrayList(GojaConfig.getAppPackPrefix()))
                .inJars(GojaConfig.getAppJars());

        for (Class cls : test_searcher.search()) {
            if (Reflect.isAbstract(cls)) {
                continue;
            }
            ClassBox.getInstance().push(cls);
        }
    }
}
