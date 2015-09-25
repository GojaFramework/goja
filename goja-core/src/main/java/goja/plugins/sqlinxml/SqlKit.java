/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.plugins.sqlinxml;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.jfinal.kit.PathKit;
import goja.config.GojaConfig;
import goja.StringPool;
import goja.kits.JaxbKit;
import goja.lang.Lang;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class SqlKit {
    /**
     * SQL XML file suffix
     */
    protected static final String CONFIG_SUFFIX = "sql.xml";


    private static final Logger logger = LoggerFactory.getLogger(SqlKit.class);

    /**
     * Management of SQL set
     */
    private static final Map<String, String> SQL_MAP = Maps.newHashMap();

    private SqlKit() {
    }

    /**
     * To obtain a configuration of SQL.
     *
     * @param groupNameAndsqlId SQL id.
     * @return sql script.
     */
    public static String sql(String groupNameAndsqlId) {

        return SQL_MAP.get(groupNameAndsqlId);
    }

    static void clearSqlMap() {
        SQL_MAP.clear();
    }

    static void putOver(String name, String value) {
        SQL_MAP.put(name, value);
    }


    static void init() {
        final String resource = PathKit.getRootClassPath() + File.separator + "sqlconf";
        initScanFiles(resource);
        if (GojaConfig.applicationMode().isDev()) {
            // 启动文件监控
            runWatch();
        }
    }

    static void reload() {
//        SQL_MAP.clear();
        final String resource = PathKit.getRootClassPath() + File.separator + "sqlconf";
        initScanFiles(resource);
    }

    private static void initScanFiles(String resource) {
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(resource));
        final List<File> files = Lists.newArrayList();
        for (File f : iterable) {
            if (f.getName().endsWith(CONFIG_SUFFIX)) {
                files.add(f);
            }
        }

        SqlGroup group;
        for (File xmlfile : files) {
            group = JaxbKit.unmarshal(xmlfile, SqlGroup.class);
            groupxmlfile(group, xmlfile.getName());
        }
        // Search Jar file xml config.
        List<String> jarlist = GojaConfig.getAppJars();
        if (!Lang.isEmpty(jarlist)) {
            String lib_path = PathKit.getWebRootPath() + File.separator + "WEB-INF" + File.separator + "lib" + File.separator;

            JarFile jarFile;
            for (String jar : jarlist) {
                String jar_path = lib_path + jar;
                try {
                    jarFile = new JarFile(jar_path);
                } catch (IOException e) {
                    logger.error("Error in finding {} the SQL configuration file", jar_path);
                    continue;
                }
                Enumeration<JarEntry> entrys = jarFile.entries();
                while (entrys.hasMoreElements()) {
                    JarEntry jarEntry = entrys.nextElement();
                    final String jar_file_name = jarEntry.getName();
                    if (jar_file_name.endsWith(CONFIG_SUFFIX)) {
                        try {
                            String xml_content = Resources.toString(Resources.getResource(jar_file_name), Charsets.UTF_8);
                            group = JaxbKit.unmarshal(xml_content, SqlGroup.class);
                            groupxmlfile(group, Files.getNameWithoutExtension(jar_file_name));
                        } catch (IOException e) {
                            logger.error("reade jar xml config has error!");
                        }
                    }
                }
            }
        }


    }

    private static void groupxmlfile(SqlGroup group, String file_name) {
        String name = group.name;
        if (StringUtils.isBlank(name)) {
            name = file_name;
        }
        for (SqlItem sqlItem : group.sqlItems) {
            final String sql_name = name + StringPool.DOT + sqlItem.id;
            if (SQL_MAP.containsKey(sql_name)) {
                logger.warn("In file {} SQL id in XML for {} has been loaded", file_name, sql_name);
                continue;
            }
            final String _val = sqlItem.value;
            if (Strings.isNullOrEmpty(_val)) {
                logger.warn("In file {} SQL id in XML for {} is empty", file_name, sql_name);
                continue;
            }
            SQL_MAP.put(sql_name, _val.replace('\r', ' ').replace('\n', ' ').replaceAll(" {2,}", " "));
        }
    }

    static void initWithTest() {

        final String resource = PathKit.getRootClassPath();
        if (Strings.isNullOrEmpty(resource)) {
            throw new NullPointerException("the resources is null.");
        }

        initScanFiles(resource.replace("test-", StringPool.EMPTY));
    }

    private static void runWatch() {
        final String path = PathKit.getRootClassPath();
        logger.info("Start the SQL configuration file scanning monitoring mechanism! path is {}", path);
        long interval = TimeUnit.SECONDS.toMillis(2);

        File config_file = new File(path);
        List<FileAlterationObserver> observerList = Lists.newArrayList();
        final File[] childrenfiles = config_file.listFiles();
        if (childrenfiles != null) {
            for (File child : childrenfiles) {
                if (child.isDirectory()) {
                    final FileAlterationObserver observer = new FileAlterationObserver(child.getAbsolutePath()
                            , FileFilterUtils.and(FileFilterUtils.fileFileFilter(), FileFilterUtils.suffixFileFilter(CONFIG_SUFFIX)
                    ), null);

                    observer.addListener(SqlXmlFileListener.me);
                    observerList.add(observer);

                }
            }
            final FileAlterationObserver observer = new FileAlterationObserver(config_file);
            observer.addListener(SqlXmlFileListener.me);
            observerList.add(observer);
        }
        // Monitoring the jar file

        List<String> jarlist = GojaConfig.getAppJars();
        if (!Lang.isEmpty(jarlist)) {
            String jar_path = PathKit.getWebRootPath() + File.separator + "WEB-INF" + File.separator + "lib" + File.separator;
            final FileAlterationObserver observer = new FileAlterationObserver(jar_path);
            observer.addListener(SqlXmlFileListener.me);
            observerList.add(observer);

        }

        final FileAlterationObserver[] observers = observerList.toArray(new FileAlterationObserver[observerList.size()]);
        FileAlterationMonitor monitor = new FileAlterationMonitor(interval, observers);

        try {
            monitor.start();
        } catch (Exception e) {
            logger.error("file monitor is error!", e);
        }

    }

    static void remove(String s) {
        SQL_MAP.remove(s);
    }
}
