package goja.rapid.excel;

public class ExcelException extends RuntimeException {

    private static final long serialVersionUID = 1685225481638059165L;

    public ExcelException(String message) {
        super(message);
    }

    public ExcelException(Throwable cause) {
        super(cause);
    }


}
