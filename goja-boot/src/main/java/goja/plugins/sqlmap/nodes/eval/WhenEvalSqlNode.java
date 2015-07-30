package goja.plugins.sqlmap.nodes.eval;

import com.google.common.base.MoreObjects;
import goja.el.El;
import goja.lang.util.Context;
import goja.plugins.sqlmap.SqlMapParams;
import goja.plugins.sqlmap.nodes.EvalSqlNode;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class WhenEvalSqlNode implements EvalSqlNode {
    public static final String WHEN = "when";
    private final String test;
    private final String sql;

    public WhenEvalSqlNode(String test, String sql) {
        this.test = test;
        this.sql = sql;
    }

    public String getTest() {
        return test;
    }

    public String getSql() {
        return sql;
    }

    @Override
    public boolean apply(SqlMapParams sqlParams,Context context) {
        context.putAll(sqlParams);
        return El.eval(context, test);
    }


    @Override
    public String rawSql() {
        return this.sql;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("sql", sql)
                .add("test", test)
                .toString();
    }
}
