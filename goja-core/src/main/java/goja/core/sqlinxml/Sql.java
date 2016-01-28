package goja.core.sqlinxml;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import goja.core.StringPool;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class Sql {

  public final String sql;

  /**
   * 是否有分页的WHERE子句 <p/> 由 Sql中的 --where-- 约定注释来判断
   */
  public final boolean where;

  /**
   * 是否有条件 <p/> 由 Sql中的 --conditions-- 约定注释来判断
   */
  public final boolean conditions;

  public final String selectSql;

  public final String whereSql;

  public Sql(SqlItem sql) {

    final String clearSql =
        sql.value.replace('\r', ' ').replace('\n', ' ').replaceAll(" {2,}", " ");

    this.selectSql = clearSql;
    String whereSql = sql.where == null ? StringPool.EMPTY
        : sql.where.value.replace('\r', ' ').replace('\n', ' ').replaceAll(" {2,}", " ");
    String conditionSql = sql.where == null ? StringPool.EMPTY
        : (sql.where.condition == null ? StringPool.EMPTY
            : sql.where.condition.value.replace('\r', ' ')
                .replace('\n', ' ')
                .replaceAll(" {2,}", " ");)

    this.where = !Strings.isNullOrEmpty(whereSql);
    this.conditions = !Strings.isNullOrEmpty(conditionSql);
    this.whereSql = whereSql;

    this.sql = clearSql + StringPool.SPACE + whereSql + conditionSql;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("sql", sql)
        .add("where", where)
        .add("conditions", conditions)
        .add("selectSql", selectSql)
        .add("whereSql", whereSql)
        .toString();
  }
}
