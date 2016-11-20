package goja.initialize;

import goja.core.app.GojaConfig;
import goja.core.app.GojaPropConst;
import goja.core.exceptions.DatabaseException;
import goja.plugins.tablebind.TableBindPlugin;
import com.jfinal.config.Plugins;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.AnsiSqlDialect;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.activerecord.dialect.PostgreSqlDialect;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.activerecord.dialect.Sqlite3Dialect;
import com.jfinal.plugin.druid.DruidPlugin;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.wall.WallFilter;

import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.Properties;

/**
 * <p> </p>
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.6
 */
public abstract class DruidDbIntializer {


    public static void init(String configName, Plugins plugins, Properties dbProp) {

        final DruidPlugin druidPlugin = druidPlugin(dbProp);
        plugins.add(druidPlugin);
        plugins.add(tableBindPlugin(configName, druidPlugin, dbProp));
    }


    /**
     * The configuration database, specify the name of the database.
     *
     * @param dbProp 数据库配置
     */
    public static DruidPlugin druidPlugin(
            Properties dbProp
    ) {

        String dbUrl = dbProp.getProperty(GojaPropConst.DBURL),
                username = dbProp.getProperty(GojaPropConst.DBUSERNAME),
                password = dbProp.getProperty(GojaPropConst.DBPASSWORD);
        if (!Strings.isNullOrEmpty(dbUrl)) {
            String dbtype = JdbcUtils.getDbType(dbUrl, StringUtils.EMPTY);
            String driverClassName;
            try {
                driverClassName = JdbcUtils.getDriverClassName(dbUrl);
            } catch (SQLException e) {
                throw new DatabaseException(e.getMessage(), e);
            }
            final DruidPlugin druidPlugin = new DruidPlugin(dbUrl, username, password, driverClassName);

            // set validator
            setValidatorQuery(dbtype, druidPlugin);
            druidPlugin.addFilter(new StatFilter());

            final String initialSize = dbProp.getProperty(GojaPropConst.DB_INITIAL_SIZE);
            if (!Strings.isNullOrEmpty(initialSize)) {
                druidPlugin.setInitialSize(MoreObjects.firstNonNull(Ints.tryParse(initialSize), 6));
            }
            final String initial_minidle = dbProp.getProperty(GojaPropConst.DB_INITIAL_MINIDLE);
            if (!Strings.isNullOrEmpty(initial_minidle)) {
                druidPlugin.setMinIdle(MoreObjects.firstNonNull(Ints.tryParse(initial_minidle), 5));
            }

            final String initial_maxwait = dbProp.getProperty(GojaPropConst.DB_INITIAL_MAXWAIT);
            if (!Strings.isNullOrEmpty(initial_maxwait)) {
                druidPlugin.setMaxWait(MoreObjects.firstNonNull(Ints.tryParse(initial_maxwait), 5));
            }
            final String initial_active = dbProp.getProperty(GojaPropConst.DB_INITIAL_ACTIVE);
            if (!Strings.isNullOrEmpty(initial_active)) {
                druidPlugin.setMaxActive(MoreObjects.firstNonNull(Ints.tryParse(initial_active), 5));
            }
            final String timeBetweenEvictionRunsMillis =
                    dbProp.getProperty(GojaPropConst.DB_TIME_BETWEEN_EVICTION_RUNS_MILLIS);
            if (!Strings.isNullOrEmpty(timeBetweenEvictionRunsMillis)) {
                final Integer millis = MoreObjects.firstNonNull(Ints.tryParse(timeBetweenEvictionRunsMillis), 10000);
                druidPlugin.setTimeBetweenEvictionRunsMillis(millis);
            }
            final String minEvictableIdleTimeMillis =
                    dbProp.getProperty(GojaPropConst.DB_MIN_EVICTABLE_IDLE_TIME_MILLIS);
            if (!Strings.isNullOrEmpty(minEvictableIdleTimeMillis)) {
                final Integer idleTimeMillis = MoreObjects.firstNonNull(Ints.tryParse(minEvictableIdleTimeMillis), 10000);
                druidPlugin.setMinEvictableIdleTimeMillis(idleTimeMillis);
            }

            final WallFilter wall = new WallFilter();
            wall.setDbType(dbtype);
            druidPlugin.addFilter(wall);
            if (GojaConfig.getPropertyToBoolean(GojaPropConst.DBLOGFILE, false)) {
                // 增加 LogFilter 输出JDBC执行的日志
                druidPlugin.addFilter(new Slf4jLogFilter());
            }


            return druidPlugin;
        }
        return null;
    }


    public static TableBindPlugin tableBindPlugin(
            String configName,
            DruidPlugin druidPlugin,
            Properties dbProp
    ) {
        String dbUrl = dbProp.getProperty(GojaPropConst.DBURL);
        if (!Strings.isNullOrEmpty(dbUrl)) {
            String dbtype = JdbcUtils.getDbType(dbUrl, StringUtils.EMPTY);
            //  setting db table name like 'dev_info'
            final TableBindPlugin atbp = new TableBindPlugin(configName, druidPlugin);

            if (!StringUtils.equals(dbtype, JdbcConstants.MYSQL)) {
                if (StringUtils.equals(dbtype, JdbcConstants.ORACLE)) {
                    atbp.setDialect(new OracleDialect());
                    atbp.setContainerFactory(new CaseInsensitiveContainerFactory(true));
                } else if (StringUtils.equals(dbtype, JdbcConstants.POSTGRESQL)) {
                    atbp.setDialect(new PostgreSqlDialect());
                    atbp.setContainerFactory(new CaseInsensitiveContainerFactory(true));
                } else if (StringUtils.equals(dbtype, JdbcConstants.H2)) {
                    atbp.setDialect(new AnsiSqlDialect());
                    atbp.setContainerFactory(new CaseInsensitiveContainerFactory(true));
                } else if (StringUtils.equals(dbtype, "sqlite")) {
                    atbp.setDialect(new Sqlite3Dialect());
                } else if (StringUtils.equals(dbtype, JdbcConstants.JTDS)) {
                    atbp.setDialect(new SqlServerDialect());
                } else {
                    System.err.println("database type is use mysql.");
                }
            }
            atbp.setShowSql(GojaConfig.getApplicationMode().isDev());
            return atbp;
        }
        return null;
    }


    public static void setValidatorQuery(String dbtype, DruidPlugin druidPlugin) {
        if (!StringUtils.equals(JdbcConstants.MYSQL, dbtype)) {
            if (StringUtils.equals(JdbcConstants.ORACLE, dbtype)) {
                druidPlugin.setValidationQuery("SELECT 1 FROM dual");
            } else if (StringUtils.equals(JdbcConstants.HSQL, dbtype)) {
                druidPlugin.setValidationQuery("SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS");
            } else if (StringUtils.equals(JdbcConstants.DB2, dbtype)) {
                druidPlugin.setValidationQuery("SELECT 1 FROM sysibm.sysdummy1");
            } else {
                druidPlugin.setValidationQuery("SELECT 1 ");
            }
        }
    }

}
