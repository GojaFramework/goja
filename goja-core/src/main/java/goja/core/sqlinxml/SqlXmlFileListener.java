/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core.sqlinxml;

import goja.core.sqlinxml.node.SqlNode;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * <p> . </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-02-06 23:45
 * @since JDK 1.6
 */
public class SqlXmlFileListener extends FileAlterationListenerAdaptor {

    public static final SqlXmlFileListener me = new SqlXmlFileListener();

    private static final Logger logger = LoggerFactory.getLogger(SqlXmlFileListener.class);

    public SqlXmlFileListener() {
    }

    @Override
    public void onDirectoryCreate(File directory) {
        logger.info("the directory {} create!", directory);
        SqlKit.reload();
    }

    @Override
    public void onDirectoryDelete(File directory) {
        logger.info("the directory {} delete!", directory);
        SqlKit.reload();
    }

    @Override
    public void onDirectoryChange(File directory) {
        logger.info("the directory {} change!", directory);
        SqlKit.reload();
    }

    private void reload(File change_file) {
        final String absolutePath = change_file.getAbsolutePath();
        if (change_file.isFile()) {
            if (absolutePath.endsWith(".jar")) {
                // Search Jar file xml config.
                String file_name = change_file.getName();
                try {
                    JarFile               jarFile = new JarFile(change_file);
                    Enumeration<JarEntry> entrys  = jarFile.entries();
                    while (entrys.hasMoreElements()) {
                        JarEntry     jarEntry    = entrys.nextElement();
                        final String jarFileName = jarEntry.getName();
                        if (jarFileName.endsWith("-sql.xml")) {
                            final List<Pair<String, SqlNode>> sqlPairSqlList = SqlParser.parseInJar(jarFileName);
                            for (Pair<String, SqlNode> sqlPair : sqlPairSqlList) {
                                SqlKit.putOver(sqlPair.getLeft(), sqlPair.getRight());
                            }
                        }
                    }
                } catch (IOException e) {
                    logger.error("Error in finding {} the SQL configuration file", file_name);
                }
            } else if (absolutePath.endsWith("-sql.xml")) {

                final List<Pair<String, SqlNode>> sqlPairSqlList = SqlParser.parseFile(change_file);
                for (Pair<String, SqlNode> sqlPair : sqlPairSqlList) {
                    SqlKit.putOver(sqlPair.getLeft(), sqlPair.getRight());
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("reload file:" + absolutePath);
                }
            }
        }
    }

    private void removeFile(File remove_file) {
        if (remove_file.isFile() && remove_file.getAbsolutePath().endsWith("-sql.xml")) {
            final List<Pair<String, SqlNode>> sqlPairSqlList = SqlParser.parseFile(remove_file);
            for (Pair<String, SqlNode> sqlPair : sqlPairSqlList) {
                SqlKit.remove(sqlPair.getLeft());
            }

            if (logger.isDebugEnabled()) {
                logger.debug("delete file:" + remove_file.getAbsolutePath());
            }
        }
    }

    @Override
    public void onFileCreate(File file) {
        logger.info("the file {} create!", file);
        reload(file);
    }

    @Override
    public void onFileChange(File file) {
        logger.info("the file {} change!", file);
        reload(file);
    }

    @Override
    public void onFileDelete(File file) {
        logger.info("the file {} delete!", file);
        removeFile(file);
    }
}
