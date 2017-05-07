package goja.mvc.handler;

import com.jfinal.handler.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import goja.core.StringPool;

/**
 * <p> </p>
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.6
 */
public class CutSessionIdHandler extends Handler {

    private static final String JSESSION_REG = ";jsessionid=[0-9a-zA-Z]+";

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        int index = target.indexOf(";jsessionid");
        if (index != -1)
            target = target.replaceAll(JSESSION_REG, StringPool.EMPTY);
        next.handle(target, request, response, isHandled);
    }
}
