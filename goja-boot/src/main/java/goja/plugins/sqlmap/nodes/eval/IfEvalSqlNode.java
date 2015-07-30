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
public class IfEvalSqlNode implements EvalSqlNode {
    public static final String IF = "if";

    private final String test;
    private final String sql;

    public IfEvalSqlNode(String test, String sql) {this.test = test;
        this.sql = sql;
    }

    public String getTest() {
        return test;
    }

    public String getSql() {
        return sql;
    }

    @Override
    public boolean apply(final SqlMapParams sqlParams,Context context) {
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
                .add("test", test)
                .add("sql", sql)
                .toString();
    }
}
