package goja.mvc.render.exception;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class CatchaException extends RuntimeException {
    private static final long serialVersionUID = -6639727802718613244L;

    public CatchaException() {
    }

    public CatchaException(String message) {
        super(message);
    }

    public CatchaException(String message, Throwable cause) {
        super(message, cause);
    }

    public CatchaException(Throwable cause) {
        super(cause);
    }

    public CatchaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
