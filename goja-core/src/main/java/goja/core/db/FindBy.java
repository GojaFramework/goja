package goja.core.db;

import goja.core.StringPool;

/**
 * <p/> Simple queries follow the following syntax [Property][Comparator]And? where Comparator can
 * be the following: <ol> <li> LessThan - less than the given value</li> <li> LessThanEquals - less
 * than or equal a give value</li> <li> GreaterThan - greater than a given value</li> <li>
 * GreaterThanEquals - greater than or equal a given value</li> <li> Like - Equivalent to a SQL like
 * expression, except that the property will always convert to lower case.</li> <li> Ilike - Similar
 * to a Like, except case insensitive, meaning that your argument will convert to lower case
 * too.</li> <li> Elike - Equivalent to a SQL like expression, no conversion.</li> <li> NotEqual -
 * Negates equality</li> <li> Equal - Equal</li> <li> Between - Between two values (requires two
 * arguments)</li> <li> IsNotNull - Not a null value (doesn’t require an argument)</li> <li> IsNull
 * - Is a null value (doesn’t require an argument)</li> </ol>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class FindBy {

    private FindBy() {
    }

    public static String findBy(String findBy) {
        return findBy(findBy, false);
    }

    public static String findBy(String findBy, boolean hsql) {
        findBy = findBy.substring(2);
        final StringBuilder sql = new StringBuilder();
        String subRequest;
        if (findBy.contains("OrderBy")) {
            subRequest = findBy.split("OrderBy")[0];
        } else {
            subRequest = findBy;
        }
        String[] parts = subRequest.split("And");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.endsWith("NotEqual")) {
                String prop = extractProp(part, "NotEqual");
                sql.append(prop).append(" <> ?");
            } else if (part.endsWith("Equal")) {
                String prop = extractProp(part, "Equal");
                sql.append(prop).append(" = ?");
            } else if (part.endsWith("IsNotNull")) {
                String prop = extractProp(part, "IsNotNull");
                sql.append(prop).append(" IS NOT NULL");
            } else if (part.endsWith("IsNull")) {
                String prop = extractProp(part, "IsNull");
                sql.append(prop).append(" IS NULL");
            } else if (part.endsWith("LessThan")) {
                String prop = extractProp(part, "LessThan");
                sql.append(prop).append(" < ?");
            } else if (part.endsWith("LessThanEquals")) {
                String prop = extractProp(part, "LessThanEquals");
                sql.append(prop).append(" <= ?");
            } else if (part.endsWith("GreaterThan")) {
                String prop = extractProp(part, "GreaterThan");
                sql.append(prop).append(" > ?");
            } else if (part.endsWith("GreaterThanEquals")) {
                String prop = extractProp(part, "GreaterThanEquals");
                sql.append(prop).append(" >= ?");
            } else if (part.endsWith("Between")) {
                String prop = extractProp(part, "Between");
                sql.append(prop).append(" BETWEEN ? AND ?");
            } else if (part.endsWith("Like")) {
                String prop = extractProp(part, "Like");
                // HSQL -> LCASE, all other dbs lower
                if (hsql) {
                    sql.append("LCASE(").append(prop).append(") LIKE ?");
                } else {
                    sql.append("LOWER(").append(prop).append(") LIKE ?");
                }
            } else if (part.endsWith("Ilike")) {
                String prop = extractProp(part, "Ilike");
                if (hsql) {
                    sql.append("LCASE(").append(prop).append(") LIKE LCASE(?").append(")");
                } else {
                    sql.append("LOWER(").append(prop).append(") LIKE LOWER(?").append(")");
                }
            } else if (part.endsWith("Elike")) {
                String prop = extractProp(part, "Elike");
                sql.append(prop).append(" LIKE ?");
            } else {
                String prop = extractProp(part, "");
                sql.append(prop).append(" = ?");
            }
            if (i < parts.length - 1) {
                sql.append(" AND ");
            }
        }
        // ORDER BY clause
        if (findBy.contains("OrderBy")) {
            sql.append(" ORDER BY ");
            String orderQuery = findBy.split("OrderBy")[1];
            parts = orderQuery.split("And");
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                String orderProp;
                if (part.endsWith("Desc")) {
                    orderProp = extractProp(part, "Desc") + " DESC";
                } else {
                    orderProp = part.toLowerCase();
                }
                if (i > 0) {
                    sql.append(", ");
                }
                sql.append(orderProp);
            }
        }
        return sql.toString();
    }

    protected static String extractProp(String part, String end) {
        String prop = part.substring(0, part.length() - end.length());
        prop = (prop.charAt(0) + StringPool.EMPTY).toLowerCase() + prop.substring(1);
        return prop;
    }
}
