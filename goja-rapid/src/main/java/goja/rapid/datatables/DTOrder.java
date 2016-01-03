package goja.rapid.datatables;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public final class DTOrder {
    private final String column;

    private final String dir;

    private DTOrder(String column, String dir) {
        this.column = column;
        this.dir = dir;
    }

    public static DTOrder create(String column, String dir) {
        return new DTOrder(column, dir);
    }

    public static DTOrder create(String column) {
        return new DTOrder(column, "asc");
    }

    public String getColumn() {
        return column;
    }

    public String getDir() {
        return dir;
    }
}
