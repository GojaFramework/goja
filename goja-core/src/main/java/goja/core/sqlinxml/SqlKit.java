/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.core.sqlinxml;

import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.jfinal.kit.PathKit;
import goja.core.StringPool;
import goja.core.app.GojaConfig;
import goja.core.sqlinxml.node.SqlNode;
import goja.core.tuples.Pair;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlKit {
  /**
   * SQL XML file suffix
   */
  protected static final String CONFIG_SUFFIX = "sql.xml";

  private static final Logger logger = LoggerFactory.getLogger(SqlKit.class);

  /**
   * Management of SQL set
   */
  private static final Map<String, SqlNode> SQL_MAP = Maps.newHashMap();

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
    FluentIterable<File> iterable =
        Files.fileTreeTraverser().breadthFirstTraversal(new File(resource));
    for (File f : iterable) {
      if (f.getName().endsWith(CONFIG_SUFFIX)) {
        final List<Pair<String, SqlNode>> fileXmlSqlList = SqlParser.parseFile(f);
        for (Pair<String, SqlNode> sqlPair : fileXmlSqlList) {
          final String sqlMapName = sqlPair.getValue0();
          if (SQL_MAP.containsKey(sqlMapName)) {
            logger.warn("sql配置文件[{}]中,已经存在[{}]的sql ID,请检查重复!", f.getAbsolutePath(), sqlMapName);
            continue;
          }
          SQL_MAP.put(sqlMapName, sqlPair.getValue1());
        }
      }
    }
    // Search Jar file xml config.
    List<String> jarlist = GojaConfig.getAppJars();
    if (!(jarlist == null || jarlist.isEmpty())) {
      String lib_path = PathKit.getWebRootPath()
          + File.separator
          + "WEB-INF"
          + File.separator
          + "lib"
          + File.separator;

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
          final String jarFileName = jarEntry.getName();
          if (jarFileName.endsWith(CONFIG_SUFFIX)) {
            final List<Pair<String, SqlNode>> parseInJar = SqlParser.parseInJar(jarFileName);
            for (Pair<String, SqlNode> sqlPair : parseInJar) {
              final String sqlMapName = sqlPair.getValue0();
              if (SQL_MAP.containsKey(sqlMapName)) {
                logger.warn("sql配置文件[{}]中,已经存在[{}]的sql ID,请检查重复!", jarFileName, sqlMapName);
                continue;
              }
              SQL_MAP.put(sqlMapName, sqlPair.getValue1());
            }
          }
        }
      }
    }
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
              , FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
              FileFilterUtils.suffixFileFilter(CONFIG_SUFFIX)
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
    if (!(jarlist == null || jarlist.isEmpty())) {
      String jar_path = PathKit.getWebRootPath()
          + File.separator
          + "WEB-INF"
          + File.separator
          + "lib"
          + File.separator;
      final FileAlterationObserver observer = new FileAlterationObserver(jar_path);
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
