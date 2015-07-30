package goja.plugins.sqlmap.nodes;

import goja.lang.util.Context;
import goja.plugins.sqlmap.SqlMapParams;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public interface EvalSqlNode {


    boolean apply(final SqlMapParams sqlParams, final Context context);


    String rawSql();
}
