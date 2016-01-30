package goja.rapid.db;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;
import goja.core.StringPool;
import goja.core.sqlinxml.node.SqlNode;
import goja.core.sqlinxml.SqlKit;
import goja.rapid.mvc.datatables.DTCriterias;
import goja.rapid.mvc.datatables.DTDao;
import goja.rapid.page.PageDto;
import java.io.Serializable;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public abstract class Dao {

    /**
     * According to the default primary key <code>id</code> is for the new data and entity.
     *
     * @param m   model
     * @param <M> Generic entity references
     * @return If for the new form of return true.
     */
    public static <M extends Model> boolean isNew(M m) {
        final Table table = TableMapping.me().getTable(m.getClass());
        final String[] pks = table.getPrimaryKey();
        return isNew(m, pks[0]);
    }

    /**
     * Query the database record set.
     *
     * @param sqlSelect SqlNode Select
     * @return query result.
     * @see SqlSelect
     * @see FindBy
     */
    public static List<Record> findBy(SqlSelect sqlSelect) {
        Preconditions.checkNotNull(sqlSelect, "The Query SqlNode is must be not null.");
        return Db.find(sqlSelect.toString(), sqlSelect.getParams().toArray());
    }

    /**
     * Query a data record.
     *
     * @param sqlSelect SqlNode Select
     * @return query result.
     * @see SqlSelect
     * @see FindBy
     */
    public static Record findOne(SqlSelect sqlSelect) {
        Preconditions.checkNotNull(sqlSelect, "The Query SqlNode is must be not null.");
        return Db.findFirst(sqlSelect.toString(), sqlSelect.getParams().toArray());
    }

    /**
     * 根据多个数据通过主键批量删除数据
     *
     * @param ids        要删除的数据值数组
     * @param modelClass 对应的Model的Class
     * @return 是否删除成功
     */
    public static <M extends Model> boolean deleteByIds(Serializable[] ids, Class<M> modelClass) {
        final Table table = TableMapping.me().getTable(modelClass);
        final String[] primaryKey = table.getPrimaryKey();
        if (primaryKey == null || primaryKey.length < 1 || ids == null) {
            throw new IllegalArgumentException("需要删除的表数据主键不存在，无法删除!");
        }
        // 暂时支持单主键的，多主键的后续在说吧。
        final String question_mark =
                StringUtils.repeat(StringPool.QUESTION_MARK, StringPool.COMMA, ids.length);
        String deleteSql = "DELETE FROM "
                + table.getName()
                + " WHERE "
                + primaryKey[0]
                + " IN ("
                + question_mark
                + ")";
        return Db.update(deleteSql, ids) >= 0;
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
        final Object val = m.get(pk_column);
        return val == null || val instanceof Number && ((Number) val).intValue() <= 0;
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
     * @param sqlPaginatePrefix 分页搜索前缀
     * @param criterias         required parameter
     * @return Paging data.
     */
    public static Page<Record> paginate(String sqlPaginatePrefix,
                                        DTCriterias criterias,
                                        List<Object> params) {
        SqlNode sqlNode = SqlKit.sqlO(sqlPaginatePrefix + ".paginate");
        Preconditions.checkNotNull(sqlNode, "[" + sqlPaginatePrefix + ".paginate]分页Sql不存在,无法执行分页");
        return DTDao.paginate(sqlNode, criterias, params);
    }

    /**
     * Paging retrieve, default sorted by id, you need to specify the datatables request parameters.
     *
     * @param sqlPaginatePrefix 分页搜索前缀
     * @param pageDto           required parameter.
     * @return Paging data.
     */
    public static Page<Record> paginate(String sqlPaginatePrefix,
                                        PageDto pageDto) {
        SqlNode sqlNode = SqlKit.sqlO(sqlPaginatePrefix + ".paginate");
        Preconditions.checkNotNull(sqlNode, "[" + sqlPaginatePrefix + ".paginate]分页Sql不存在,无法执行分页");
        String where = sqlNode.whereSql;
        int pageSize = pageDto.pageSize;
        int p = pageDto.page;
        int start = ((p - 1) * pageSize) + 1;
        final List<RequestParam> params = pageDto.params;
        final List<Object> query_params = pageDto.query_params;
        if ((params.isEmpty()) && (query_params.isEmpty())) {
            return Db.paginate(start, pageSize, sqlNode.selectSql, where);
        } else {

            if (!params.isEmpty()) {
                where += (sqlNode.conditions ? StringPool.SPACE : " WHERE 1=1 ");
            }
            for (RequestParam param : pageDto.params) {
                where += param.toSql();
            }
            return Db.paginate(start, pageSize, sqlNode.selectSql, where, query_params.toArray());
        }
    }

    /**
     * generating oracle paginate SQL.
     *
     * @param sql sql.
     * @return paginate sql.
     */
    public static String oraclePaginateSql(String sql) {
        return String.format(
                "SELECT * FROM (SELECT goja_ptbs.*,ROWNUM num FROM (%s) goja_ptbs WHERE ROWNUM <= ? ) WHERE num >= ?",
                sql);
    }
}
