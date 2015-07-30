package goja.rapid.db;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public enum Condition {
    NE(" <> ?"),
    EQ(" = ?"),
    INN(" is not null"),
    IN(" is null"),
    LT(" < ?"),
    LTEQ(" <= ?"),
    GT(" > ?"),
    GTEQ(" >= ?"),
    BETWEEN(" BETWEEN ? AND ?"),
    LIKE(" LIKE ?"),
    LLIKE(" LIKE ?"),
    RLIKE(" LIKE ?");

    public final String condition;

    Condition(String condition) {
        this.condition = condition;
    }
}
