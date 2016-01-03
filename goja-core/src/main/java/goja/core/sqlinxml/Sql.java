package goja.core.sqlinxml;

import com.google.common.base.MoreObjects;
import goja.core.StringPool;
import goja.core.app.GojaPropConst;
import org.apache.commons.lang3.StringUtils;

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

    public Sql(String sql) {
        this.sql = sql;

        this.selectSql = StringUtils.substringBefore(sql, GojaPropConst.WHERESPLIT);
        String whereSql = StringUtils.substringAfter(sql, GojaPropConst.WHERESPLIT);

        this.where = StringUtils.containsIgnoreCase(sql, GojaPropConst.WHERESPLIT);
        this.conditions = StringUtils.containsIgnoreCase(whereSql, GojaPropConst.CONDITIONSSPLIT);
        this.whereSql = StringUtils.replace(whereSql, GojaPropConst.CONDITIONSSPLIT, StringPool.SPACE);
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
