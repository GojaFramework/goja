package goja.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Global interceptor annotation.
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-01-04 13:14
 * @since JDK 1.6
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface AppInterceptor {
}
