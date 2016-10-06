/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.test;

import goja.Goja;
import goja.core.app.GojaConfig;
import goja.core.exceptions.DatabaseException;
import goja.core.kits.reflect.Reflect;
import goja.core.sqlinxml.SqlKit;
import goja.initialize.ctxbox.ClassFinder;
import goja.plugins.tablebind.AutoTableBindPlugin;
import com.jfinal.plugin.activerecord.dialect.AnsiSqlDialect;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.activerecord.dialect.PostgreSqlDialect;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.activerecord.dialect.Sqlite3Dialect;
import com.jfinal.plugin.druid.DruidPlugin;

import com.google.common.collect.Lists;

import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.wall.WallFilter;

import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * <p> Model Test case. </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-10-03 20:30
 * @since JDK 1.6
 */
public abstract class ModelTestCase {

    private static final List<DruidPlugin>         dpList               = Lists.newArrayList();
    private static final List<AutoTableBindPlugin> autoTableBindPlugins = Lists.newArrayList();


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        GojaConfig.init();

        GojaConfig.getConfigProps();
        ClassFinder.findWithTest();
        Reflect.on(Goja.class).call("initWithTest");

        final Map<String, Properties> dbConfig = GojaConfig.loadDBConfig(GojaConfig.getConfigProps());
        for (String db_config : dbConfig.keySet()) {
            final Properties db_props = dbConfig.get(db_config);
            if (db_props != null && !db_props.isEmpty()) {
                final String dbUrl = db_props.getProperty("db.url");
                String dbtype = JdbcUtils.getDbType(dbUrl, StringUtils.EMPTY);
                final DruidPlugin druidPlugin = configDatabasePlugins(
                        dbUrl,
                        db_props.getProperty("db.username"),
                        db_props.getProperty("db.password"));


                AutoTableBindPlugin activeRecord = new AutoTableBindPlugin(db_config, druidPlugin);
                if (!StringUtils.equals(dbtype, JdbcConstants.MYSQL)) {
                    if (StringUtils.equals(dbtype, JdbcConstants.ORACLE)) {
                        activeRecord.setDialect(new OracleDialect());
                    } else if (StringUtils.equals(dbtype, JdbcConstants.POSTGRESQL)) {
                        activeRecord.setDialect(new PostgreSqlDialect());
                    } else if (StringUtils.equals(dbtype, JdbcConstants.H2)) {
                        activeRecord.setDialect(new AnsiSqlDialect());
                    } else if (StringUtils.equals(dbtype, "sqlite")) {
                        activeRecord.setDialect(new Sqlite3Dialect());
                    } else if (StringUtils.equals(dbtype, JdbcConstants.JTDS)) {
                        activeRecord.setDialect(new SqlServerDialect());
                    } else {
                        System.err.println("database type is use mysql.");
                    }
                }

                dpList.add(druidPlugin);
                activeRecord.setShowSql(true);
                autoTableBindPlugins.add(activeRecord);
            }
        }

        for (DruidPlugin druidPlugin : dpList) {
            druidPlugin.start();
        }
        for (AutoTableBindPlugin autoTableBindPlugin : autoTableBindPlugins) {
            autoTableBindPlugin.start();
        }

        if (GojaConfig.getPropertyToBoolean("db.sqlinxml", true)) {
            Reflect.on(SqlKit.class).call("initWithTest");
        }
    }

    /**
     * 配置数据库插件，用于测试用例的测试方法启动前
     *
     * @param db_url      数据库链接地址
     * @param db_username 数据库链接用户
     * @param db_password 数据库链接密码
     */
    private static DruidPlugin configDatabasePlugins(String db_url, String db_username,
                                                     String db_password) {
        String dbtype = JdbcUtils.getDbType(db_url, StringUtils.EMPTY);
        String driverClassName;
        try {
            driverClassName = JdbcUtils.getDriverClassName(db_url);
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        }
        DruidPlugin dp = new DruidPlugin(db_url
                , db_username
                , db_password
                , driverClassName);

        //        dp.addFilter(new StatFilter());
        WallFilter wall = new WallFilter();
        wall.setDbType(dbtype);
        dp.addFilter(wall);
        // set validator
        if (!StringUtils.equals(JdbcConstants.MYSQL, dbtype)) {
            if (StringUtils.equals(JdbcConstants.ORACLE, dbtype)) {
                dp.setValidationQuery("SELECT 1 FROM dual");
            } else if (StringUtils.equals(JdbcConstants.HSQL, dbtype)) {
                dp.setValidationQuery("SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS");
            } else if (StringUtils.equals(JdbcConstants.DB2, dbtype)) {
                dp.setValidationQuery("SELECT 1 FROM sysibm.sysdummy1");
            } else {
                dp.setValidationQuery("SELECT 1 ");
            }
        }

        dp.getDataSource();


        return dp;
    }

    @AfterClass
    public static void setUpAfterClass() throws Exception {
        for (AutoTableBindPlugin autoTableBindPlugin : autoTableBindPlugins) {
            autoTableBindPlugin.stop();
        }
        for (DruidPlugin druidPlugin : dpList) {
            druidPlugin.stop();
        }
    }
}
