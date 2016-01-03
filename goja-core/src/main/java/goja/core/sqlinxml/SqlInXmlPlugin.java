/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.core.sqlinxml;

import com.jfinal.plugin.IPlugin;

public class SqlInXmlPlugin implements IPlugin {

    public SqlInXmlPlugin() {
    }

    @Override
    public boolean start() {
        SqlKit.init();
        return true;
    }

    @Override
    public boolean stop() {
        SqlKit.clearSqlMap();
        return true;
    }
}
