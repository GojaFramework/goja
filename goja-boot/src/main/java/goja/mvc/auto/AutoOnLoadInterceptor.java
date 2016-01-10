package goja.mvc.auto;

import com.jfinal.aop.Interceptor;
import com.jfinal.config.Interceptors;
import goja.core.annotation.AppInterceptor;
import goja.initialize.ctxbox.ClassBox;
import goja.initialize.ctxbox.ClassType;
import goja.logging.Logger;

import java.util.List;

/**
 * <p> Interceptor annotation scan. </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-01-04 13:11
 * @since JDK 1.6
 */
public class AutoOnLoadInterceptor {

    /**
     * jfinal 全局拦截器
     */
    private final Interceptors interceptors;

    /**
     * 构造函数，指定全局拦截器
     *
     * @param interceptors jfinal 全局拦截器
     */
    public AutoOnLoadInterceptor(Interceptors interceptors) {
        this.interceptors = interceptors;
    }

    public void load() {
        List<Class> interceptorClass = ClassBox.getInstance().getClasses(ClassType.AOP);
        if (interceptorClass != null && !interceptorClass.isEmpty()) {
            AppInterceptor interceptor;

            for (Class interceptorClas : interceptorClass) {
                interceptor = (AppInterceptor) interceptorClas.getAnnotation(AppInterceptor.class);
                if (interceptor != null) {
                    try {
                        interceptors.add((Interceptor) interceptorClas.newInstance());
                    } catch (InstantiationException | IllegalAccessException e) {
                        Logger.error("instance aop interceptor is error!", e);
                        throw new IllegalArgumentException("instance aop interceptor is error!");
                    }
                }
            }
        }
    }
}
