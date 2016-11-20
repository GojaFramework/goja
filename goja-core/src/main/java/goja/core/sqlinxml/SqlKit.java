/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.core.sqlinxml;

import goja.core.StringPool;
import goja.core.app.GojaConfig;
import goja.core.sqlinxml.node.SqlNode;
import com.jfinal.kit.PathKit;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.lang3.tuple.Pair;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class SqlKit {

    private static final Logger logger = LoggerFactory.getLogger(SqlKit.class);

    /**
     * Management of SQL set
     */
    private static final Map<String, SqlNode> SQL_MAP = Maps.newHashMap();


    private static final Pattern sqlConfigFilePattern = Pattern.compile(".*-sql\\.xml");

    private SqlKit() {
    }

    /**
     * To obtain a configuration of SQL.
     *
     * @param groupNameAndsqlId SQL id.
     * @return sql script.
     */
    public static String sql(String groupNameAndsqlId) {

        final SqlNode sqlNode = SQL_MAP.get(groupNameAndsqlId);
        return sqlNode == null ? StringPool.EMPTY : sqlNode.sql;
    }

    public static SqlNode sqlNode(String groupNameAndsqlId) {

        return SQL_MAP.get(groupNameAndsqlId);
    }

    static void clearSqlMap() {
        SQL_MAP.clear();
    }

    static void putOver(String name, SqlNode value) {
        SQL_MAP.put(name, value);
    }

    static void init() {
        final String resource = PathKit.getRootClassPath() + File.separator + "sqlconf";

        initScanFiles(resource);
        if (GojaConfig.getApplicationMode().isDev()) {
            // 启动文件监控
            runWatch();
        }
    }

    static void initWithTest() {

        final String resource = PathKit.getRootClassPath();
        if (Strings.isNullOrEmpty(resource)) {
            throw new NullPointerException("the resources is null.");
        }

        initScanFiles(resource.replace("test-", StringPool.EMPTY) + File.separator + "sqlconf");
        initScanFiles(resource + File.separator + "sqlconf");
    }

    static void reload() {
        SQL_MAP.clear();
        final String resource = PathKit.getRootClassPath() + File.separator + "sqlconf";
        initScanFiles(resource);
    }

    private static void initScanFiles(String resource) {

        final Reflections reflections = new Reflections(resource);

        final Set<String> sqlConfigResources = reflections.getResources(sqlConfigFilePattern);

        readConfigFile(sqlConfigResources);

        for (String appScan : GojaConfig.getAppScans()) {
            final Reflections appScanReflection = new Reflections(appScan);

            final Set<String> sqlConfigScanResources = appScanReflection.getResources(sqlConfigFilePattern);

            readConfigFile(sqlConfigScanResources);
        }
    }

    private static void readConfigFile(Set<String> sqlConfigScanResources) {
        for (String sqlConfigResource : sqlConfigScanResources) {
            final File sqlConfigFile = new File(sqlConfigResource);

            final List<Pair<String, SqlNode>> fileXmlSqlList = SqlParser.parseFile(sqlConfigFile);
            for (Pair<String, SqlNode> sqlPair : fileXmlSqlList) {
                final String sqlMapName = sqlPair.getLeft();
                if (SQL_MAP.containsKey(sqlMapName)) {
                    logger.warn("sql配置文件[{}]中,已经存在[{}]的sql ID,请检查重复!",
                            sqlConfigFile.getAbsolutePath(), sqlMapName);
                    continue;
                }
                SQL_MAP.put(sqlMapName, sqlPair.getRight());
            }
        }
    }


    private static void runWatch() {
        final String path = PathKit.getRootClassPath();
        logger.info("Start the SQL configuration file scanning monitoring mechanism! path is {}", path);
        long interval = TimeUnit.SECONDS.toMillis(2);

        final File   config_file   = new File(path);
        final File[] childrenfiles = config_file.listFiles();

        final List<FileAlterationObserver> observerList = Lists.newArrayList();

        if (childrenfiles != null) {
            for (File child : childrenfiles) {
                if (child.isDirectory()) {
                    final FileAlterationObserver observer = new FileAlterationObserver(child.getAbsolutePath()
                            , FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
                            FileFilterUtils.suffixFileFilter("-sql.xml")
                    ), null);

                    observer.addListener(SqlXmlFileListener.me);
                    observerList.add(observer);
                }
            }
            final FileAlterationObserver observer = new FileAlterationObserver(config_file);
            observer.addListener(SqlXmlFileListener.me);
            observerList.add(observer);
        }

        final FileAlterationObserver[] observers =
                observerList.toArray(new FileAlterationObserver[observerList.size()]);
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
