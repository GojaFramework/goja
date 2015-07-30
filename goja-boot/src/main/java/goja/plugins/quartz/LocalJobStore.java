/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 jfinal app. jfapp Group.
 */

package goja.plugins.quartz;

import com.jfinal.plugin.activerecord.DbKit;
import org.quartz.SchedulerConfigException;
import org.quartz.impl.jdbcjobstore.JobStoreCMT;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerSignaler;
import org.quartz.utils.ConnectionProvider;
import org.quartz.utils.DBConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * <p>
 * .
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-06-23 16:17
 * @since JDK 1.6
 */
public class LocalJobStore extends JobStoreCMT {
    /**
     * Name used for the transactional ConnectionProvider for Quartz.
     * This provider will delegate to the local Spring-managed DataSource.
     *
     * @see org.quartz.utils.DBConnectionManager#addConnectionProvider
     */
    public static final String TX_DATA_SOURCE_PREFIX = "gojaTxDataSource.";

    /**
     * Name used for the non-transactional ConnectionProvider for Quartz.
     * This provider will delegate to the local Spring-managed DataSource.
     *
     * @see org.quartz.utils.DBConnectionManager#addConnectionProvider
     */
    public static final String NON_TX_DATA_SOURCE_PREFIX = "gojaNonTxDataSource.";


    @Override
    public void initialize(ClassLoadHelper loadHelper, SchedulerSignaler signaler)
            throws SchedulerConfigException {


        // Configure transactional connection settings for Quartz.
        setDataSource(TX_DATA_SOURCE_PREFIX + getInstanceName());
        setDontSetAutoCommitFalse(true);

        // Register transactional ConnectionProvider for Quartz.
        DBConnectionManager.getInstance().addConnectionProvider(
                TX_DATA_SOURCE_PREFIX + getInstanceName(),
                new ConnectionProvider() {
                    @Override
                    public Connection getConnection() throws SQLException {
                        // Return a transactional Connection, if any.
                        return DbKit.getConfig().getConnection();
                    }

                    @Override
                    public void shutdown() {
                    }

                    public void initialize() {
                    }
                }
        );


        setNonManagedTXDataSource(NON_TX_DATA_SOURCE_PREFIX + getInstanceName());

        // Register non-transactional ConnectionProvider for Quartz.
        DBConnectionManager.getInstance().addConnectionProvider(
                NON_TX_DATA_SOURCE_PREFIX + getInstanceName(),
                new ConnectionProvider() {
                    @Override
                    public Connection getConnection() throws SQLException {
                        // Always return a non-transactional Connection.
                        return DbKit.getConfig().getDataSource().getConnection();
                    }

                    @Override
                    public void shutdown() {
                    }

                    public void initialize() {
                    }
                }
        );

        super.initialize(loadHelper, signaler);

    }

}
