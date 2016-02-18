package goja.core.db;

import java.util.List;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class SqlSelect extends SqlQuery {

    /**
     * Select 语句
     */
    protected final SqlConcat select;
    /**
     * FROM 语句
     */
    protected final SqlConcat from;
    /**
     * JOIN  语句
     */
    protected final SqlConcat join;
    /**
     * Where SQL
     */
    protected final SqlConcat where;
    /**
     * Group By SqlNode script.
     */
    protected final SqlConcat groupBy;
    /**
     * Order By SqlNode script.
     */
    protected final SqlConcat orderBy;
    /**
     * limit sql.
     */
    protected final SqlConcat limit;

    private SqlSelect() {
        select = new SqlConcat("SELECT ", ", ").defaultValue(null);
        from = new SqlConcat("FROM ", ", ").defaultValue(null);
        join = new SqlConcat(null, null).defaultValue(null);
        where = new SqlConcat("WHERE ", null).defaultValue(null);
        groupBy = new SqlConcat("GROUP BY ", ", ").defaultValue(null);
        orderBy = new SqlConcat("ORDER BY ", ", ").defaultValue(null);
        limit = new SqlConcat("LIMIT ", null);
    }

    private SqlSelect(SqlSelect src) {
        select = new SqlConcat(src.select);
        from = new SqlConcat(src.from);
        join = new SqlConcat(src.join);
        where = new SqlConcat(src.where);
        groupBy = new SqlConcat(src.groupBy);
        orderBy = new SqlConcat(src.orderBy);
        limit = new SqlConcat(src.limit);

        params.addAll(src.getParams());
    }

    /**
     * Link SqlNode Select
     *
     * @return SQL SELECT
     */
    public static SqlSelect create() {
        return new SqlSelect();
    }

    public static SqlSelect create(SqlSelect src) {
        return new SqlSelect(src);
    }

    @Override
    public SqlSelect param(Object obj) {
        super.param(obj);
        return this;
    }

    @Override
    public SqlSelect params(Object... objs) {
        super.params(objs);
        return this;
    }

    public SqlSelect select(String... expr) {
        select.add(expr);
        return this;
    }

    public SqlSelect from(String... expr) {
        from.add(expr);
        return this;
    }

    public SqlSelect innerJoin(String... expr) {
        join.prefix("INNER JOIN ").separator(" INNER JOIN ").add(expr);
        return this;
    }

    public SqlSelect leftJoin(String... expr) {
        join.prefix("LEFT JOIN ").separator(" LEFT JOIN ").add(expr);
        return this;
    }

    public SqlSelect where(String... expr) {
        return andWhere(expr);
    }

    public SqlSelect andWhere(String... expr) {
        where.separator(" AND ").add(expr);
        return this;
    }

    public SqlSelect orWhere(String... expr) {
        where.separator(" OR ").add(expr);
        return this;
    }

    public SqlSelect groupBy(String... expr) {
        groupBy.add(expr);
        return this;
    }

    public SqlSelect orderBy(String... expr) {
        orderBy.add(expr);
        return this;
    }

    public SqlSelect limit(long lines) {
        limit.append(lines);
        return this;
    }

    public SqlSelect limit(long offset, long lines) {
        limit.append(offset + ", " + lines);
        return this;
    }

    public Where where() {
        return new Where(this);
    }

    public SqlSelect where(Where... expr) {
        return andWhere(expr);
    }

    public SqlSelect andWhere(Where... expr) {
        for (Where subquery : expr) andWhere(subquery.toString());
        return this;
    }

    public SqlSelect orWhere(Where... expr) {
        for (Where subquery : expr) orWhere(subquery.toString());
        return this;
    }

    @Override
    public String toString() {
        if (select.isEmpty() || from.isEmpty()) throw new IllegalArgumentException();
        return new SqlConcat("", " ").defaultValue(null)
                .append(select)
                .append(from)
                .append(join)
                .append(where)
                .append(groupBy)
                .append(orderBy)
                .append(limit)
                .toString();
    }

    public static class Where {
        private final SqlSelect parent;
        private final SqlConcat where;

        private Where(SqlSelect parent) {
            this.parent = parent;
            where = new SqlConcat("(", null, ")").defaultValue(null);
        }

        public Where param(Object obj) {
            parent.param(obj);
            return this;
        }

        public Where params(Object... objs) {
            parent.params(objs);
            return this;
        }

        public List<Object> getParams() {
            return parent.getParams();
        }

        public int paramCurrentIndex() {
            return parent.paramCurrentIndex();
        }

        public String pmark() {
            return parent.pmark();
        }

        public String pmark(int offset) {
            return parent.pmark(offset);
        }

        public Where where(String... expr) {
            return andWhere(expr);
        }

        public Where andWhere(String... expr) {
            where.separator(" AND ").add(expr);
            return this;
        }

        public Where orWhere(String... expr) {
            where.separator(" OR ").add(expr);
            return this;
        }

        @Override
        public String toString() {
            return where.toString();
        }
    }
}
