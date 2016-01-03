package goja.rapid.datatables;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public final class DTSearch {
    private final String value;
    private final boolean regex;

    private DTSearch(String value, boolean regex) {
        this.value = value;
        this.regex = regex;
    }

    public static DTSearch create(String value, boolean regex) {
        return new DTSearch(value, regex);
    }

    public static DTSearch create(String value) {
        return create(value, false);
    }

    public String getValue() {
        return value;
    }

    public boolean isRegex() {
        return regex;
    }
}
