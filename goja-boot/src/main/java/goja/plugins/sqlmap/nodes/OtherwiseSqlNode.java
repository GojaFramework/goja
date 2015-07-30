package goja.plugins.sqlmap.nodes;

import com.google.common.base.MoreObjects;
import goja.lang.util.Context;
import goja.plugins.sqlmap.SqlMapParams;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
 class OtherwiseSqlNode implements EvalSqlNode {

    public static final String OTHERWISE = "otherwise";

    private final String sql;

    public OtherwiseSqlNode(String sql) {this.sql = sql;}

    @Override
    public boolean apply(SqlMapParams sqlParams,Context context) {
        return true;
    }

    @Override
    public String rawSql() {
        return sql;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("sql", sql)
                .toString();
    }
}
