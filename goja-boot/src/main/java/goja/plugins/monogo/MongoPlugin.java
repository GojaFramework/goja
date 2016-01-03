/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.plugins.monogo;

import com.google.common.base.Strings;
import com.jfinal.plugin.IPlugin;
import com.mongodb.MongoClient;

public class MongoPlugin implements IPlugin {

    public static final int DEFAUL_PORT = 27017;
    public static final String DEFAULT_PKGS = "app.entitys";
    private static MongoClient client;
    private final String host;
    private final int port;
    private final String database;
    private final String morphia_pkgs;

    public MongoPlugin(String host, int port, String database, String morphia_pkgs) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.morphia_pkgs = morphia_pkgs;
    }

    @Override
    public boolean start() {

        client = new MongoClient(host, port);

        MongoKit.init(client, database);
        if (!Strings.isNullOrEmpty(morphia_pkgs)) {
            MorphiaKit.create(client, database, morphia_pkgs);
        }
        return true;
    }

    @Override
    public boolean stop() {
        if (client != null) {
            client.close();
        }
        return true;
    }
}
