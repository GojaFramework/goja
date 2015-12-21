package goja.core.lambda;

import java.lang.reflect.Method;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public interface Dynamic {

    Object call(Method m, Object[] args);
}
