package goja.rapid.db;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

import static goja.core.StringPool.*;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public abstract class SqlQuery {

    protected final List<Object> params;

    protected SqlQuery() {
        params = Lists.newArrayList();
    }

    public static String quote(String str) {
        return SINGLE_QUOTE + str.replace(SINGLE_QUOTE, "\\'") + SINGLE_QUOTE;
    }

    public static String inlineParam(Object param) {
        if (param == null) return "NULL";

        String str;
        if (param instanceof String) {
            str = quote(param.toString());
        } else if (param instanceof Iterable<?>) {
            SqlConcat list = new SqlConcat(LEFT_BRACKET, ", ", RIGHT_BRACKET);
            for (Object p : (Iterable<?>) param) list.append(inlineParam(p));
            str = list.toString();
        } else if (param instanceof Object[]) {
            SqlConcat list = new SqlConcat(LEFT_BRACKET, ", ", RIGHT_BRACKET);
            for (Object p : (Object[]) param) list.append(inlineParam(p));
            str = list.toString();
        } else if (param instanceof Enum<?>) {
            str = quote(param.toString());
        } else {
            str = param.toString();
        }
        return str;
    }

    public static String whereIn(String column, Object param) {
        String value = inlineParam(param);
        if (value.length() == 0) return value;

        String operator;
        if (param instanceof Object[]) {
            operator = " IN ";
        } else if (param instanceof Iterable<?>) {
            operator = " IN ";
        } else {
            operator = EQUALS;
        }

        return column + operator + value;
    }

    public SqlQuery param(Object obj) {
        params.add(obj);
        return this;
    }

    public SqlQuery params(Object... objs) {
        Collections.addAll(params, objs);
        return this;
    }

    public List<Object> getParams() {
        return params;
    }

    public int paramCurrentIndex() {
        return params.size() + 1;
    }

    public String pmark() {
        return QUESTION_MARK + Integer.toString(paramCurrentIndex());
    }

    public String pmark(int offset) {
        return QUESTION_MARK + Integer.toString(paramCurrentIndex() + offset);
    }
}
