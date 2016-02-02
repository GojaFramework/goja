package goja.rapid.mvc.kisso;

import com.baomidou.kisso.SSOConfig;
import com.baomidou.kisso.SSOHelper;
import com.baomidou.kisso.Token;
import com.baomidou.kisso.web.interceptor.KissoAbstractInterceptor;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> </p>
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.6
 */
public final class SSOInterceptor extends KissoAbstractInterceptor implements Interceptor {

  private static final Logger logger = LoggerFactory.getLogger("SSOInterceptor");

  @Override public void intercept(Invocation inv) {
    // 正常执行
    HttpServletRequest request = inv.getController().getRequest();
    HttpServletResponse response = inv.getController().getResponse();
    Token token = SSOHelper.getToken(request);
    if (token == null) {
      if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
        // Handler 处理 AJAX 请求
        getHandlerInterceptor().preTokenIsNullAjax(request, response);
      } else if ("APP".equals(request.getHeader("PLATFORM"))) {
        /* Handler 处理 APP接口调用 请求
         * 没有修改kisso核心代码，直接使用Ajax的认证判断方式，如果未认证，返回401状态码
				 */
        getHandlerInterceptor().preTokenIsNullAjax(request, response);
        logger.info("request from APP invoke");
      } else {
        try {
          logger.info("logout. request url:" + request.getRequestURL());
          SSOHelper.clearRedirectLogin(request, response);
        } catch (IOException e) {
          logger.error("清理出错!", e);
        }
      }
    } else {
      // 正常请求，request 设置 token 减少二次解密
      request.setAttribute(SSOConfig.SSO_TOKEN_ATTR, token);
      inv.invoke();
    }
  }
}
