package goja.core.annotation;

import java.lang.annotation.*;

/**
 * <p> Global interceptor annotation. </p>
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
