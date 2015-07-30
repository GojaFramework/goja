package goja.plugins.sqlmap.exceptions;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class SqlMapException extends RuntimeException {
    private static final long serialVersionUID = 4044081842902102101L;

    public SqlMapException() {
    }

    public SqlMapException(String message) {
        super(message);
    }

    public SqlMapException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlMapException(Throwable cause) {
        super(cause);
    }

    public SqlMapException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
