package goja.core.exceptions;

/**
 * <p>Error in application.conf </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class ConfigurationException extends GojaException {

    public ConfigurationException(String message) {
        super(message);
    }
    @Override
    public String getErrorTitle() {
        return getMessage();
    }

    @Override
    public String getErrorDescription() {
        return "Configuration error.";
    }
}
