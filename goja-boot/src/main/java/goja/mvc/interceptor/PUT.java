package goja.mvc.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class PUT implements Interceptor {
    public void intercept(Invocation ai) {
        Controller controller = ai.getController();
        if ("PUT".equalsIgnoreCase(controller.getRequest().getMethod().toUpperCase())) {
            ai.invoke();
        } else {
            controller.renderError(404);
        }
    }
}
