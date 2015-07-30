package goja.plugins.sqlmap.nodes;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import goja.lang.Lang;
import goja.lang.util.Context;
import goja.plugins.sqlmap.SqlMapParams;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class SqlNode {

    private final String id;

    private final String method;

    private final String rawSql;

    private final List<EvalSqlNode> evalSqlNodes = Lists.newArrayList();

    public SqlNode(String id, String method, String rawSql) {
        this.id = id;
        this.method = method;
        this.rawSql = rawSql;
    }

    protected SqlNode addEvalSqlNode(EvalSqlNode evalSqlNode) {
        this.evalSqlNodes.add(evalSqlNode);
        return this;
    }


    public List<EvalSqlNode> getEvalSqlNodes() {
        return evalSqlNodes;
    }


    public String getId() {
        return id;
    }

    public String getMethod() {
        return method;
    }

    public String sql(SqlMapParams sqlParams) {
        final Context context = Lang.context();
        final List<EvalSqlNode> evalSqlNodes = getEvalSqlNodes();
        StringBuilder raw_sql_builder = new StringBuilder(rawSql);
        for (EvalSqlNode evalSqlNode : evalSqlNodes) {
            if (evalSqlNode.apply(sqlParams, context)) {
                raw_sql_builder.append(StringUtils.SPACE).append(evalSqlNode.rawSql()).append(StringUtils.SPACE);
            }
        }
        return raw_sql_builder.toString();
    }




    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("method", method)
                .add("rawSql", rawSql)
                .add("evalSqlNodes", evalSqlNodes)
                .toString();
    }
}
