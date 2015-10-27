package goja.rapid.db;

import static goja.core.StringPool.EMPTY;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class SqlConcat {

    private String prefix, separator, suffix;
    private String defaultValue;
    private String expr;

    public SqlConcat(String prefix, String separator, String suffix) {
        this.prefix = prefix;
        this.separator = separator;
        this.suffix = suffix;
        this.defaultValue = EMPTY;
        this.expr = EMPTY;
    }

    public SqlConcat(String prefix, String separator) {
        this(prefix, separator, EMPTY);
    }

    public SqlConcat(SqlConcat src) {
        this.prefix = src.prefix;
        this.separator = src.separator;
        this.suffix = src.suffix;
        this.defaultValue = src.defaultValue;
        this.expr = src.expr;
    }

    public SqlConcat defaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public SqlConcat prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public SqlConcat separator(String separator) {
        this.separator = separator;
        return this;
    }

    public SqlConcat append(Object obj) {
        final String text;
        if (obj != null) {
            String objStr = obj.toString();
            if (objStr.length() > 0) text = objStr;
            else text = defaultValue;
        } else text = defaultValue;

        if (text != null) {
            if (expr.length() > 0) {
                if (separator == null) throw new NullPointerException();
                expr += separator;
            }
            expr += text;
        }
        return this;
    }

    public SqlConcat add(String... texts) {
        for (String text : texts) append(text);
        return this;
    }

    public boolean isEmpty() {
        return expr.length() <= 0;
    }

    @Override
    public String toString() {
        if (isEmpty()) return EMPTY;
        if (prefix == null || suffix == null) throw new NullPointerException();
        return prefix + expr + suffix;
    }
}
