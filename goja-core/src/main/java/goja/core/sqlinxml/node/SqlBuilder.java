package goja.core.sqlinxml.node;

import com.google.common.base.MoreObjects;

import goja.core.StringPool;

public class SqlBuilder {
    private boolean where;
    private boolean condition;
    private String  selectSql;
    private String  whereSql;

    private String conditionSql;

    public SqlBuilder setConditionSql(String conditionSql) {
        this.conditionSql = conditionSql;
        return this;
    }

    public SqlBuilder setWhere(boolean where) {
        this.where = where;
        return this;
    }

    public SqlBuilder setCondition(boolean condition) {
        this.condition = condition;
        return this;
    }

    public SqlBuilder setSelectSql(String selectSql) {
        this.selectSql = selectSql;
        return this;
    }

    public SqlBuilder setWhereSql(String whereSql) {
        this.whereSql = whereSql;
        return this;
    }

    public SqlNode createSql() {
        final String conditionSql = MoreObjects.firstNonNull(this.conditionSql, StringPool.EMPTY);
        final String whereSql = MoreObjects.firstNonNull(this.whereSql, StringPool.EMPTY);
        final String selectSql = MoreObjects.firstNonNull(this.selectSql, StringPool.EMPTY);
        return new SqlNode(selectSql
                + StringPool.SPACE
                + whereSql,
                where, condition,
                conditionSql, selectSql, whereSql);
    }
}