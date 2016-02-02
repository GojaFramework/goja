package goja.core.sqlinxml;

import com.google.common.base.MoreObjects;
import goja.core.StringPool;
import goja.core.sqlinxml.node.SqlNode;
import goja.core.sqlinxml.node.WhereNode;

class SqlBuilder {
  private boolean where;
  private boolean conditions;
  private String selectSql;
  private String whereSql;

  private String conditionSql;

  private WhereNode whereNode;

  public SqlBuilder setWhereNode(WhereNode whereNode) {
    this.whereNode = whereNode;
    return this;
  }

  public SqlBuilder setConditionSql(String conditionSql) {
    this.conditionSql = conditionSql;
    return this;
  }

  public SqlBuilder setWhere(boolean where) {
    this.where = where;
    return this;
  }

  public SqlBuilder setConditions(boolean conditions) {
    this.conditions = conditions;
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
        + whereSql
      /*  + StringPool.SPACE
        + conditionSql*/,
        where, conditions, whereNode,
        conditionSql, selectSql, whereSql);
  }
}