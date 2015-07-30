package goja.plugins.sqlmap.nodes;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class SqlMapNode {


    private final String model;

    private final String packageName;

    private final List<SqlNode> sqlNodes = Lists.newArrayList();

    public SqlMapNode(String model, String packageName) {
        this.model = model;
        this.packageName = Strings.isNullOrEmpty(packageName) ? "app.models" : packageName;
    }

    public SqlMapNode addSqlNode(SqlNode sqlNode) {
        this.sqlNodes.add(sqlNode);
        return this;
    }

    public String getModel() {
        return model;
    }

    public List<SqlNode> getSqlNodes() {
        return sqlNodes;
    }

    public String getPackageName() {
        return packageName;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("model", model)
                .add("packageName", packageName)
                .add("sqlNodes", sqlNodes)
                .toString();
    }
}
