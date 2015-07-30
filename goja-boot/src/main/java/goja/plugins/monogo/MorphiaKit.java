/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.plugins.monogo;

import com.google.common.base.Preconditions;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

/**
 * <p>
 * MongoDb morphia framework kit.
 * </p>
 *
 * @author walter yang
 * @version 1.0 2013-12-05 10:10
 * @since JDK 1.5
 */
public final class MorphiaKit {


    private static Datastore _datastore;

    /**
     * Default .
     *
     * @param mongo          mongo
     * @param db             mongo database.
     * @param entity_package entity package name.
     */
    private MorphiaKit(final MongoClient mongo, final String db, String entity_package) {
        Preconditions.checkNotNull(mongo, "the mongo object is not null. ");
        Preconditions.checkNotNull(db, "the mongodb database name is not null.");
        Morphia morphia = new Morphia();
        _datastore = morphia.createDatastore(mongo, db);
        morphia.mapPackage(entity_package);
    }

    public static MorphiaKit create(final MongoClient mongo, final String db, String entity_package) {
        if (MorphiaKit.getDataStore() != null) {
            throw new IllegalStateException();
        } else {
            return new MorphiaKit(mongo, db, entity_package);
        }
    }


    public static <T> Key<T> save(T t) {
        Preconditions.checkNotNull(_datastore, "the  morphia datasotre is not instace!");
        return _datastore.save(t);
    }

    /**
     * query mongod entity
     *
     * @param cls entity class
     * @param <T> paramter object cls
     * @return Query https://github.com/mongodb/morphia/wiki/Query
     */
    public static <T> Query<T> query(Class<T> cls) {
        return _datastore.find(cls);
    }

    public static <T> T get(Class<T> cls) {
        return _datastore.find(cls).get();
    }

    public static <T> T get(Class<T> cls, Object id) {
        return _datastore.get(cls, id);
    }


    /**
     * https://github.com/mongodb/morphia/wiki/Datastore
     *
     * @return datastore
     */
    public static Datastore getDataStore() {
        return _datastore;
    }
}
