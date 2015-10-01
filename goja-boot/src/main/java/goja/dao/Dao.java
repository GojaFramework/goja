package goja.dao;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import goja.StringPool;
import goja.castor.Castors;
import goja.plugins.sqlinxml.SqlKit;
import goja.rapid.datatables.DTCriterias;
import goja.rapid.datatables.DTDao;
import goja.rapid.db.FindBy;
import goja.rapid.db.RequestParam;
import goja.rapid.db.SqlSelect;
import goja.rapid.page.PageDto;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public abstract class Dao {

    public static final String SQL_PIRFIX_WHERE   = ".where";
    public static final String SQL_PIRFIX_COLUMNS = ".column";


    /**
     * Gets the data values by specifying the data type, and automatically convert types
     *
     * @param m    Model
     * @param attr database table field.
     * @param cls  data type.
     * @param <T>  specifying the data type.
     * @param <M>  model type.
     * @return data value.
     */
    public static <T, M extends Model> T getData(M m, String attr, Class<T> cls) {
        Object value = m.get(attr);
        return Castors.me().castTo(value, cls);
    }


    /**
     * According to the default primary key <code>id</code> is for the new data and entity.
     *
     * @param m   model
     * @param <M> Generic entity references
     * @return If for the new form of return true.
     */
    public static <M extends Model> boolean isNew(M m) {
        return isNew(m, StringPool.PK_COLUMN);
    }

    /**
     * Query the database record set.
     *
     * @param sqlSelect Sql Select
     * @return query result.
     * @see SqlSelect
     * @see FindBy
     */
    public static List<Record> findBy(SqlSelect sqlSelect) {
        Preconditions.checkNotNull(sqlSelect, "The Query Sql is must be not null.");
        return Db.find(sqlSelect.toString(), sqlSelect.getParams().toArray());
    }

    /**
     * Query a data record.
     *
     * @param sqlSelect Sql Select
     * @return query result.
     * @see SqlSelect
     * @see FindBy
     */
    public static Record findOne(SqlSelect sqlSelect) {
        Preconditions.checkNotNull(sqlSelect, "The Query Sql is must be not null.");
        return Db.findFirst(sqlSelect.toString(), sqlSelect.getParams().toArray());
    }



    /**
     * According to the primary key and entity determine whether for the new entity.
     *
     * @param m         Database model.
     * @param pk_column PK Column.
     * @param <M>       Generic entity references.
     * @return If for the new form of return true.
     */
    public static <M extends Model> boolean isNew(M m, String pk_column) {
        final Number number = m.getNumber(pk_column);
        return number == null || number.intValue() <= 0;
    }

    /**
     * Paging retrieve, default sorted by id, you need to specify the datatables request parameters.
     *
     * @param model_name sql-conf sqlgroup name.
     * @param criterias  required parameter
     * @return Paging data.
     */
    public static Page<Record> paginate(String model_name,
                                        DTCriterias criterias) {
        return paginate(model_name, criterias, Lists.newArrayListWithCapacity(1));
    }

    /**
     * Paging retrieve, default sorted by id, you need to specify the datatables request parameters.
     *
     * @param model_name sql-conf sqlgroup name.
     * @param criterias  required parameter
     * @return Paging data.
     */
    public static Page<Record> paginate(String model_name,
                                        DTCriterias criterias,
                                        List<Object> params) {
        return DTDao.paginate(SqlKit.sql(model_name + SQL_PIRFIX_WHERE)
                , SqlKit.sql(model_name + SQL_PIRFIX_COLUMNS)
                , criterias, params);
    }

    /**
     * Paging retrieve, default sorted by id, you need to specify the datatables request parameters.
     *
     * @param where       FROM WHERE SQL.
     * @param sql_columns SELECT column sql.
     * @param pageDto     required parameter.
     * @return Paging data.
     */
    public static Page<Record> paginate(String where,
                                        String sql_columns,
                                        PageDto pageDto) {
        where = Strings.nullToEmpty(where);
        int pageSize = pageDto.pageSize;
        int p = pageDto.page;
        int start = ((p - 1) * pageSize) + 1;
        final List<RequestParam> params = pageDto.params;
        final List<Object> query_params = pageDto.query_params;
        if ((params == null || params.isEmpty()) && (query_params == null || query_params.isEmpty())) {
            return Db.paginate(start, pageSize, sql_columns, where);
        } else {
            if (!StringUtils.containsIgnoreCase(where, "WHERE")) {
                where = where + " WHERE 1=1 ";
            }
            for (RequestParam param : pageDto.params) {
                where += param.toSql();
            }

            return Db.paginate(start, pageSize, sql_columns, where, query_params.toArray());
        }
    }


    /**
     * generating oracle paginate SQL.
     *
     * @param sql sql.
     * @return paginate sql.
     */
    public static String oraclePaginateSql(String sql) {
        return String.format("SELECT * FROM (SELECT goja_ptbs.*,ROWNUM num FROM (%s) goja_ptbs WHERE ROWNUM <= ? ) WHERE num >= ?", sql);
    }

}
