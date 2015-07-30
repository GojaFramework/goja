package goja.plugins.sqlmap.nodes;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import goja.lang.util.Context;
import goja.plugins.sqlmap.SqlMapParams;
import goja.plugins.sqlmap.nodes.eval.WhenEvalSqlNode;

import java.util.List;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
 class ChooseSqlNode implements EvalSqlNode{
    public static final String CHOOSE = "choose";

    private final List<WhenEvalSqlNode> whenEvalSqlNodes = Lists.newArrayList();

    private OtherwiseSqlNode otherwiseSqlNode ;

    public ChooseSqlNode() {
    }

    public ChooseSqlNode addWhenEvalSqlNode(WhenEvalSqlNode whenEvalSqlNode){
        this.whenEvalSqlNodes.add(whenEvalSqlNode);
        return this;
    }

    public ChooseSqlNode setOtherwiseSqlNode(OtherwiseSqlNode otherwiseSqlNode) {
        this.otherwiseSqlNode = otherwiseSqlNode;
        return this;
    }


    private String rawSql;


    @Override
    public boolean apply(SqlMapParams sqlParams, Context context) {
        if (whenEvalSqlNodes.isEmpty() && otherwiseSqlNode == null) {
            return false;
        }
        boolean whenOk = false;

        StringBuilder sqlBuilder = new StringBuilder();

        for (WhenEvalSqlNode whenEvalSqlNode : whenEvalSqlNodes) {
            if (whenEvalSqlNode.apply(sqlParams, context)) {
                sqlBuilder.append(whenEvalSqlNode.rawSql());
            } else {
                whenOk = true;
            }
        }
        if (whenOk) {
            if (otherwiseSqlNode == null) {
                whenOk = false;
            } else {
                rawSql = otherwiseSqlNode.rawSql();
            }
        } else {
            rawSql = sqlBuilder.toString();
        }


        return whenOk;
    }

    @Override
    public String rawSql() {
        return rawSql;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("whenEvalSqlNodes", whenEvalSqlNodes)
                .add("otherwiseSqlNode", otherwiseSqlNode)
                .toString();
    }
}
