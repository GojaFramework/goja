package goja.core.sqlinxml.node;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.List;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class SqlNode {

  public final String sql;

  /**
   * 是否有分页的WHERE子句
   */
  public final boolean where;

  /**
   * 是否有条件
   */
  public final boolean conditions;

  public final List<ConditionNode> conditionNodeList = Lists.newArrayList();
  public final WhereNode whereNode;

  public final String conditionSql;

  public final String selectSql;

  public final String whereSql;

  public SqlNode(String sql, boolean where, boolean conditions, WhereNode whereNode,
      String conditionSql, String selectSql, String whereSql) {
    this.sql = sql;
    this.where = where;
    this.conditions = conditions;
    this.whereNode = whereNode;
    this.conditionSql = conditionSql;
    this.selectSql = selectSql;
    this.whereSql = whereSql;
  }

  public void addCondition(ConditionNode conditionNode) {
    this.conditionNodeList.add(conditionNode);
  }
  public void addCondition(List<ConditionNode> conditionNodes) {
    this.conditionNodeList.addAll(conditionNodes);
  }




  @Override public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("sql", sql)
        .add("where", where)
        .add("conditions", conditions)
        .add("selectSql", selectSql)
        .add("whereSql", whereSql)
        .toString();
  }
}
