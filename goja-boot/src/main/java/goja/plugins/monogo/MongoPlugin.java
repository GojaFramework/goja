/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.plugins.monogo;

import com.jfinal.plugin.IPlugin;
import com.mongodb.MongoClient;

public class MongoPlugin implements IPlugin {

    public static final int    DEFAUL_PORT  = 27017;
    public static final String DEFAULT_PKGS = "app.entitys";

    private final  String      host;
    private final  int         port;
    private final  String      database;
    private final  String      morphia_pkgs;
    /**
     * enable morphia
     */
    private final  boolean     morphia;
    private static MongoClient client;


    public MongoPlugin(String host, int port, String database, boolean morphia, String morphia_pkgs) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.morphia = morphia;
        this.morphia_pkgs = morphia_pkgs;
    }

    @Override
    public boolean start() {

        client = new MongoClient(host, port);

        MongoKit.init(client, database);
        if (morphia) {
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
