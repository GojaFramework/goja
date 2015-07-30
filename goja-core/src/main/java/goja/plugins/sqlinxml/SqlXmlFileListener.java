/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.plugins.sqlinxml;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import goja.GojaConfig;
import goja.kits.JaxbKit;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static goja.StringPool.DOT;

/**
 * <p>
 * .
 * </p>
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
        SqlGroup group;
        if (change_file.isFile()) {
            if (absolutePath.endsWith(".jar")) {
                // Search Jar file xml config.
                List<String> jarlist = GojaConfig.getAppJars();
                String file_name = change_file.getName();
                if (jarlist.contains(file_name)) {
                    try {
                        JarFile jarFile = new JarFile(change_file);
                        Enumeration<JarEntry> entrys = jarFile.entries();
                        while (entrys.hasMoreElements()) {
                            JarEntry jarEntry = entrys.nextElement();
                            final String jar_file_name = jarEntry.getName();
                            if (jar_file_name.endsWith(SqlKit.CONFIG_SUFFIX)) {
                                try {
                                    String xml_content = Resources.toString(Resources.getResource(jar_file_name), Charsets.UTF_8);
                                    group = JaxbKit.unmarshal(xml_content, SqlGroup.class);
                                    String name = group.name;
                                    if (StringUtils.isBlank(name)) {
                                        name = change_file.getName();
                                    }
                                    for (SqlItem sqlItem : group.sqlItems) {
                                        SqlKit.putOver(name + DOT + sqlItem.id, sqlItem.value);
                                    }
                                } catch (IOException e) {
                                    logger.error("reade jar xml config has error!");
                                }
                            }
                        }
                    } catch (IOException e) {
                        logger.error("Error in finding {} the SQL configuration file", file_name);
                    }
                }
            } else if (absolutePath.endsWith(SqlKit.CONFIG_SUFFIX)) {
                group = JaxbKit.unmarshal(change_file, SqlGroup.class);
                String name = group.name;
                if (StringUtils.isBlank(name)) {
                    name = change_file.getName();
                }
                for (SqlItem sqlItem : group.sqlItems) {
                    SqlKit.putOver(name + DOT + sqlItem.id, sqlItem.value);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("reload file:" + absolutePath);
                }
            }
        }

    }

    private void removeFile(File remove_file) {
        SqlGroup group;
        if (remove_file.isFile() && remove_file.getAbsolutePath().endsWith(SqlKit.CONFIG_SUFFIX)) {
            group = JaxbKit.unmarshal(remove_file, SqlGroup.class);
            String name = group.name;
            if (StringUtils.isBlank(name)) {
                name = remove_file.getName();
            }
            for (SqlItem sqlItem : group.sqlItems) {
                SqlKit.remove(name + DOT + sqlItem.id);
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
