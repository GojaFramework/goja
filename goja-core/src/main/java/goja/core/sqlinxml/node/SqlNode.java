package goja.core.sqlinxml.node;

import com.google.common.base.MoreObjects;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public final class SqlNode {

    public static final String WHERE_MARKER     = "-- @where";
    public static final String CONDITION_MARKER = "-- @condition";


    public final String sql;

    /**
     * 是否有分页的WHERE子句
     */
    public final boolean where;

    /**
     * 是否有条件
     */
    public final boolean condition;

    public final String conditionSql;

    public final String selectSql;

    public final String whereSql;


    public SqlNode(String sql, boolean where, boolean condition,
                   String conditionSql, String selectSql, String whereSql) {
        this.sql = sql;

        this.where = where;
        this.condition = condition;
        this.conditionSql = conditionSql;
        this.selectSql = selectSql;
        this.whereSql = whereSql;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("sql", sql)
                .add("where", where)
                .add("condition", condition)
                .add("selectSql", selectSql)
                .add("whereSql", whereSql)
                .toString();
    }
}
