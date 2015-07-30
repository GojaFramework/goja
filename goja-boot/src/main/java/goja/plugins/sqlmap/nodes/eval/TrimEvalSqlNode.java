package goja.plugins.sqlmap.nodes.eval;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import goja.lang.util.Context;
import goja.plugins.sqlmap.SqlMapParams;
import goja.plugins.sqlmap.nodes.EvalSqlNode;

import java.util.List;

import static goja.StringPool.SPACE;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class TrimEvalSqlNode implements EvalSqlNode {

    public static final String TRIM = "trim";

    public static final String PREFIX = "prefix";

    private final String prefix;

    private final String prefixOverrides;


    public TrimEvalSqlNode(String prefix, String prefixOverrides) {
        this.prefix = prefix;
        this.prefixOverrides = prefixOverrides;
    }


    private final List<EvalSqlNode> evalSqlNodes = Lists.newArrayList();

    public TrimEvalSqlNode addEvalSqlNode(EvalSqlNode evalSqlNode) {
        this.evalSqlNodes.add(evalSqlNode);
        return this;
    }

    private String rawSql;

    @Override
    public boolean apply(SqlMapParams sqlParams, Context context) {
        if (evalSqlNodes.isEmpty()) {
            return false;
        }
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(SPACE).append(this.prefix).append(SPACE);

        boolean prefixOver = false;
        for (EvalSqlNode evalSqlNode : evalSqlNodes) {
            if (evalSqlNode.apply(sqlParams, context)) {
                if (prefixOver) {
                    sqlBuilder.append(SPACE).append(this.prefixOverrides)
                            .append(SPACE).append(evalSqlNode.rawSql()).append(SPACE);
                } else {
                    sqlBuilder.append(SPACE).append(evalSqlNode.rawSql()).append(SPACE);
                    prefixOver = true;
                }
            }
        }
        rawSql = sqlBuilder.toString();
        return true;
    }

    @Override
    public String rawSql() {
        return rawSql;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("prefix", prefix)
                .add("prefixOverrides", prefixOverrides)
                .add("evalSqlNodes", evalSqlNodes)
                .toString();
    }
}
