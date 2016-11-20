package goja.mvc.interceptor;

import goja.core.kits.reflect.ClassPathScanning;
import com.jfinal.aop.Interceptor;
import com.jfinal.config.Interceptors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * <p> Interceptor annotation scan. </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-01-04 13:11
 * @since JDK 1.6
 */
public abstract class AutoInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AutoInterceptor.class);

    public static void setAppInterceptor(final Interceptors interceptors) {
        Set<Class<?>> interceptorClass = ClassPathScanning.scanAnnotation(AppInterceptor.class);
        if (CollectionUtils.isNotEmpty(interceptorClass)) {
            for (Class interceptorClas : interceptorClass) {
                if (logger.isDebugEnabled()) {
                    logger.debug("GOJA: Initializes the global interceptors [{}]", interceptorClas);
                }
                try {
                    interceptors.add((Interceptor) interceptorClas.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("GOJAERROR: Initializes the global interceptors [{}]!", interceptorClas, e);
                    throw new IllegalArgumentException("instance aop interceptor is error!");
                }
            }
        }
    }
}
