/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.test;

import goja.Goja;
import goja.core.app.GojaConfig;
import goja.core.kits.reflect.Reflect;
import goja.core.sqlinxml.SqlKit;
import goja.initialize.DruidDbIntializer;
import goja.plugins.tablebind.TableBindPlugin;
import com.jfinal.plugin.druid.DruidPlugin;

import com.google.common.collect.Lists;

import org.junit.AfterClass;
import org.junit.BeforeClass;

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

    private static final List<DruidPlugin> dpList = Lists.newArrayList();

    private static final List<TableBindPlugin> TABLE_BIND_PLUGINs = Lists.newArrayList();


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        GojaConfig.init();

        GojaConfig.getConfigProps();
        Reflect.on(Goja.class).call("initWithTest");

        final Map<String, Properties> dbConfig = GojaConfig.loadDBConfig(GojaConfig.getConfigProps());
        for (String db_config : dbConfig.keySet()) {
            final Properties db_props = dbConfig.get(db_config);
            if (db_props != null && !db_props.isEmpty()) {
                final DruidPlugin druidPlugin = DruidDbIntializer.druidPlugin(db_props);
                dpList.add(druidPlugin);
                TABLE_BIND_PLUGINs.add(DruidDbIntializer.tableBindPlugin(db_config, druidPlugin, db_props));
            }
        }

        for (DruidPlugin druidPlugin : dpList) {
            druidPlugin.start();
        }
        for (TableBindPlugin tableBindPlugin : TABLE_BIND_PLUGINs) {
            tableBindPlugin.start();
        }

        if (GojaConfig.getPropertyToBoolean("db.sqlinxml", true)) {
            Reflect.on(SqlKit.class).call("initWithTest");
        }
    }

    @AfterClass
    public static void setUpAfterClass() throws Exception {
        for (TableBindPlugin tableBindPlugin : TABLE_BIND_PLUGINs) {
            tableBindPlugin.stop();
        }
        for (DruidPlugin druidPlugin : dpList) {
            druidPlugin.stop();
        }
    }
}
