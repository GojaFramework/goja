package goja.rapid.datatables;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;
import goja.core.StringPool;
import goja.rapid.db.Condition;
import goja.core.tuples.Triplet;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

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

        final List<Triplet<String, Condition, Object>> custom_params = criterias.getParams();
        if (!custom_params.isEmpty()) {
            boolean append_and = StringUtils.containsIgnoreCase(where, "WHERE");
            if (!append_and) {
                where_sql.append(" WHERE ");
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

    private static void itemCustomParamSql(List<Object> params, StringBuilder where_sql, List<Triplet<String, Condition, Object>> custom_params, boolean append_and) {
        for (Triplet<String, Condition, Object> custom_param : custom_params) {
            if (append_and) {
                where_sql.append(" AND ");
            }
            where_sql.append(custom_param.getValue0());
            final Condition con = custom_param.getValue1();
            where_sql.append(con.condition);
            switch (con) {
                case BETWEEN:
                    final Object[] value2 = (Object[]) custom_param.getValue2();
                    params.add(value2[0]);
                    params.add(value2[1]);
                    break;
                default:
                    params.add(custom_param.getValue2());
                    break;
            }
            append_and = true;
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

        final List<Triplet<String, Condition, Object>> custom_params = criterias.getParams();
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


    public static void appendWhereSql(List<Object> params, StringBuilder where, List<Triplet<String, Condition, Object>> custom_params) {
        if (!custom_params.isEmpty()) {
            where.append(" WHERE ");
            itemCustomParamSql(params, where, custom_params, false);
        }
    }
}
