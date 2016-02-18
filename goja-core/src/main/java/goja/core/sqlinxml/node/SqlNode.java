package goja.core.sqlinxml.node;

import com.google.common.base.MoreObjects;
import goja.core.StringPool;
import ru.lanwen.verbalregex.VerbalExpression;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public final class SqlNode {

  public static final String WHERE_MARKER = "-- @where";
  public static final String CONDITION_MARKER = "-- @condition";

  /**
   * 原始SQL
   */
  public final String originalSql;

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

  /**
   * if 指令
   */
  private final IfMarker ifMarker;

  public final boolean useIf;

  public SqlNode(String sql, boolean where, boolean condition,
      String conditionSql, String selectSql, String whereSql) {
    this.originalSql = sql;
    this.sql = sql;
    this.useIf = useIf();
    ifMarker = this.useIf ? new IfMarker(this.originalSql) : null;

    this.where = where;
    this.condition = condition;
    this.conditionSql = conditionSql;
    this.selectSql = selectSql;
    this.whereSql = whereSql;
  }

  public IfMarker getIfMarker() {
    return ifMarker;
  }

  public boolean useIf() {
    final VerbalExpression ifRegex = VerbalExpression.regex()
        .anything()
        .then("--")
        .then(StringPool.SPACE)
        .oneOrMore()
        .then("#_if")
        .build();
    System.out.println("ifRegex = " + ifRegex);
    return ifRegex
        .test(originalSql);
  }

  @Override public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("sql", sql)
        .add("where", where)
        .add("condition", condition)
        .add("selectSql", selectSql)
        .add("whereSql", whereSql)
        .toString();
  }
}
