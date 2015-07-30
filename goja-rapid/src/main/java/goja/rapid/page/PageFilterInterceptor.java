package goja.rapid.page;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

/**
 * <p>
 * 查询分页过滤器.
 * <p/>
 * usage:
 * <code>
 * <form>
 * <input type="text" name="s-username">
 * <input type="text" name="s-create-time-between">
 * <input type="text" name="s-create-time-and">
 * </form>
 * </code>
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-08-17 16:11
 * @since JDK 1.6
 */
public class PageFilterInterceptor implements Interceptor {
    public static final  String FILTER_PAGE   = "psf";
    private static final String SEARCH_PARAMS = "sp_url";

    public void intercept(Invocation ai) {
        final Controller controller = ai.getController();
        final PageDto pageDto = PageDto.create(controller);
        controller.setAttr(FILTER_PAGE, pageDto);
        controller.setAttr(SEARCH_PARAMS, pageDto.getQueryUrl());
        ai.invoke();
    }

}
