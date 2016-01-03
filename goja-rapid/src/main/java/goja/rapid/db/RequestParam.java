package goja.rapid.db;

/**
 * <p> . </p>
 *
 * @author sagyf
 * @version 2015-02-07
 * @since JDK 1.6
 */
public class RequestParam {
    public final String key;
    public final Condition condition;

    public RequestParam(String key, String condition) {
        this.key = key;
        this.condition = Condition.valueOf(condition);
    }

    public String toSql() {
        return String.format(" AND %s %s", key, condition.condition);
    }

    public enum Direction {
        ASC,
        DESC
    }
}
