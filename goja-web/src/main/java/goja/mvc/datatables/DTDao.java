package goja.mvc.datatables;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

import goja.core.StringPool;
import goja.core.db.Condition;
import goja.core.sqlinxml.SqlKit;
import goja.core.sqlinxml.node.SqlNode;

import static goja.core.StringPool.COMMA;
import static goja.core.StringPool.SPACE;

/**
 * <p> Jquery datatables database query retrieval </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public final class DTDao {

    public static final String SQL_WHERE = " WHERE ";


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
        SqlNode sqlNode = SqlKit.sqlNode(sqlPaginatePrefix + ".paginate");
        Preconditions.checkNotNull(sqlNode, "[" + sqlPaginatePrefix + ".paginate]分页Sql不存在,无法执行分页");
        return DTDao.paginate(sqlNode, criterias, params);
    }

    public static Page<Record> paginate(SqlNode sqlNode,
                                        DTCriterias criterias,
                                        List<Object> params) {
        return paginate(sqlNode.whereSql
                        + (sqlNode.condition ? StringPool.SPACE : " WHERE 1=1 "),
                sqlNode.selectSql, criterias, params);
    }

    /**
     * Paging retrieve, default sorted by id, you need to specify the datatables request parameters.
     *
     * @param where       FROM WHERE SQL.
     * @param sql_columns SELECT column sql.
     * @param criterias   required parameter
     * @return Paging data.
     */
    public static Page<Record> paginate(String where,
                                        String sql_columns,
                                        DTCriterias criterias,
                                        List<Object> params) {
        int pageSize = criterias.getLength();
        int start = criterias.getStart() / pageSize + 1;

        StringBuilder where_sql = new StringBuilder(where);

        final List<Triple<String, Condition, Object>> custom_params = criterias.getParams();
        if (!custom_params.isEmpty()) {
            boolean append_and = StringUtils.containsIgnoreCase(where, "WHERE");
            if (!append_and) {
                where_sql.append(SQL_WHERE);
            }
            itemCustomParamSql(params, where_sql, custom_params, append_and);
        }

        final List<DTOrder> order = criterias.getOrder();
        if (order != null && !order.isEmpty()) {
            StringBuilder orderBy = new StringBuilder();
            for (DTOrder _order : order)
                orderBy.append(_order.getColumn()).append(StringPool.SPACE).append(_order.getDir());
            final String byColumns = orderBy.toString();
            if (!Strings.isNullOrEmpty(byColumns)) {
                where_sql.append(" ORDER BY ").append(byColumns);
            }
        }

        if (params == null || params.isEmpty()) {
            return Db.paginate(start, pageSize, sql_columns, where_sql.toString());
        } else {

            return Db.paginate(start, pageSize, sql_columns, where_sql.toString(), params.toArray());
        }
    }

    /**
     * 分页检索，默认按照id进行排序，需要指定datatables的请求参数。
     *
     * @param model     实体
     * @param criterias 请求参数
     * @return 分页数据
     */
    public static Page<Record> paginate(Class<? extends Model> model, DTCriterias criterias) {
        int pageSize = criterias.getLength();
        int start = criterias.getStart() / pageSize + 1;

        final Table table = TableMapping.me().getTable(model);
        final String tableName = table.getName();

        final List<DTColumn> columns = criterias.getColumns();

        String sql_columns = null;
        if (!(columns == null || columns.isEmpty())) {
            StringBuilder sql_builder = new StringBuilder("SELECT ");
            boolean first = false;
            for (DTColumn column : columns) {
                if (column != null) {
                    if (first) {
                        sql_builder.append(COMMA).append(SPACE).append(column.getData());
                    } else {
                        sql_builder.append(column.getData());
                        first = true;
                    }
                }
            }
            sql_columns = sql_builder.toString();
        }

        StringBuilder where = new StringBuilder(" FROM ");
        where.append(tableName).append(SPACE);

        //        final DTSearch search = criterias.getSearch();

        final List<Triple<String, Condition, Object>> custom_params = criterias.getParams();
        final List<Object> params = Lists.newArrayList();
        appendWhereSql(params, where, custom_params);

        final List<DTOrder> order = criterias.getOrder();
        if (!(order == null || order.isEmpty())) {
            StringBuilder orderBy = new StringBuilder();
            for (DTOrder _order : order)
                orderBy.append(_order.getColumn()).append(SPACE).append(_order.getDir());
            final String byColumns = orderBy.toString();
            if (!Strings.isNullOrEmpty(byColumns)) {
                where.append(" ORDER BY ").append(byColumns);
            }
        }

        return Db.paginate(start, pageSize, sql_columns, where.toString(), params.toArray());
    }

    public static void appendWhereSql(List<Object> params, StringBuilder where,
                                      List<Triple<String, Condition, Object>> custom_params) {
        if (!custom_params.isEmpty()) {
            where.append(SQL_WHERE);
            itemCustomParamSql(params, where, custom_params, false);
        }
    }

    public static StringBuilder appendWhereSql(List<Object> params,
                                               SqlNode sqlNode,
                                               List<Triple<String, Condition, Object>> custom_params) {
        StringBuilder where =
                new StringBuilder(sqlNode.whereSql);
        if (!custom_params.isEmpty()) {
            where.append(sqlNode.condition ? " AND " : SQL_WHERE);
            itemCustomParamSql(params, where, custom_params, false);
        }
        return where;
    }

    private static void itemCustomParamSql(List<Object> params, StringBuilder where_sql,
                                           List<Triple<String, Condition, Object>> custom_params,
                                           boolean append_and) {
        for (Triple<String, Condition, Object> custom_param : custom_params) {
            if (append_and) {
                where_sql.append(" AND ");
            }
            where_sql.append(custom_param.getLeft());
            final Condition con = custom_param.getMiddle();
            where_sql.append(con.condition);
            switch (con) {
                case BETWEEN:
                    final Object[] value2 = (Object[]) custom_param.getRight();
                    params.add(value2[0]);
                    params.add(value2[1]);
                    break;
                default:
                    params.add(custom_param.getRight());
                    break;
            }
            append_and = true;
        }
    }
}
