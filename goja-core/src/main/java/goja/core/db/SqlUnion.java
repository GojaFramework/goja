package goja.core.db;

import static goja.core.StringPool.*;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class SqlUnion extends SqlQuery {
    private final SqlConcat union;
    private final SqlConcat orderBy;
    private final SqlConcat limit;

    public SqlUnion() {
        union = new SqlConcat(EMPTY, null);
        orderBy = new SqlConcat("ORDER BY ", ", ").defaultValue(null);
        limit = new SqlConcat("LIMIT ", null);
    }

    public SqlUnion(SqlUnion src) {
        union = new SqlConcat(src.union);
        orderBy = new SqlConcat(src.orderBy);
        limit = new SqlConcat(src.limit);

        params.addAll(src.getParams());
    }

    @Override
    public SqlUnion param(Object obj) {
        super.param(obj);
        return this;
    }

    @Override
    public SqlUnion params(Object... objs) {
        super.params(objs);
        return this;
    }

    public SqlUnion orderBy(String... expr) {
        orderBy.add(expr);
        return this;
    }

    public SqlUnion limit(long lines) {
        limit.append(lines);
        return this;
    }

    public SqlUnion limit(long offset, long lines) {
        limit.append(offset + ", " + lines);
        return this;
    }

    private void unionSep(String separator, SqlSelect... expr) {
        for (SqlSelect query : expr) {
            String sql = query.toString();
            if (sql.length() > 0) sql = LEFT_BRACKET + sql + RIGHT_BRACKET;
            union.separator(separator).append(sql);
            params.addAll(query.getParams());
        }
    }

    public SqlUnion union(SqlSelect... expr) {
        unionSep(" UNION ", expr);
        return this;
    }

    public SqlUnion unionAll(SqlSelect... expr) {
        unionSep(" UNION ALL ", expr);
        return this;
    }

    @Override
    public String toString() {
        return union.isEmpty() ? EMPTY : new SqlConcat(EMPTY, " ").defaultValue(null)
                .append(union)
                .append(orderBy)
                .append(limit)
                .toString();
    }
}
