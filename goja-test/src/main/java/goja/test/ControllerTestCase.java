package goja.test;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.io.Files;
import com.jfinal.config.JFinalConfig;
import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;
import goja.Goja;
import goja.core.kits.reflect.Reflect;
import goja.initialize.ctxbox.ClassFinder;
import goja.test.mock.MockHttpRequest;
import goja.test.mock.MockHttpResponse;
import goja.test.mock.MockServletContext;
import org.junit.AfterClass;
import org.junit.Before;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public abstract class ControllerTestCase {

    protected static ServletContext servletContext = new MockServletContext();
    protected static MockHttpRequest request;
    protected static MockHttpResponse response;
    protected static Handler handler;
    private static boolean configStarted = false;
    private static JFinalConfig configInstance;
    private String actionUrl;
    private String bodyData;
    private File bodyFile;
    private File responseFile;
    private Class<Goja> config;

    @SuppressWarnings("unchecked")
    public ControllerTestCase() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        Preconditions.checkArgument(genericSuperclass instanceof ParameterizedType,
                "Your ControllerTestCase must have genericType");
        config = Goja.class;
    }

    private static void initConfig(JFinal me, ServletContext servletContext, JFinalConfig config) {
        Reflect.on(me).call("init", config, servletContext);
    }

    public static void start(Class<Goja> configClass) throws Exception {
        if (configStarted) {
            return;
        }

        ClassFinder.findWithTest();
        Reflect.on(Goja.class).call("initWithTest");

        JFinal me = JFinal.me();
        configInstance = configClass.newInstance();
        initConfig(me, servletContext, configInstance);
        handler = Reflect.on(me).get("handler");
        configStarted = true;
        configInstance.afterJFinalStart();
    }

    @AfterClass
    public static void stop() throws Exception {
        configInstance.beforeJFinalStop();
    }

    public Object findAttrAfterInvoke(String key) {
        return request.getAttribute(key);
    }

    private String getTarget(String url, MockHttpRequest request) {
        String target = url;
        if (url.contains("?")) {
            target = url.substring(0, url.indexOf("?"));
            String queryString = url.substring(url.indexOf("?") + 1);
            String[] keyVals = queryString.split("&");
            for (String keyVal : keyVals) {
                int i = keyVal.indexOf('=');
                String key = keyVal.substring(0, i);
                String val = keyVal.substring(i + 1);
                request.setParameter(key, val);
            }
        }
        return target;
    }

    @Before
    public void init() throws Exception {
        start(config);
    }

    public String invoke() {
        if (bodyFile != null) {
            List<String> req = null;
            try {
                req = Files.readLines(bodyFile, Charsets.UTF_8);
            } catch (IOException e) {
                Throwables.propagate(e);
            }
            bodyData = Joiner.on("").join(req);
        }
        StringWriter resp = new StringWriter();
        request = new MockHttpRequest(bodyData);
        response = new MockHttpResponse(resp);
        Reflect.on(handler)
                .call("handle", getTarget(actionUrl, request), request, response, new boolean[]{true});
        String response = resp.toString();
        if (responseFile != null) {
            try {
                Files.write(response, responseFile, Charsets.UTF_8);
            } catch (IOException e) {
                Throwables.propagate(e);
            }
        }
        return response;
    }

    public ControllerTestCase post(File bodyFile) {
        this.bodyFile = bodyFile;
        return this;
    }

    public ControllerTestCase post(String bodyData) {
        this.bodyData = bodyData;
        return this;
    }

    public ControllerTestCase use(String actionUrl) {
        this.actionUrl = actionUrl;
        return this;
    }

    public ControllerTestCase writeTo(File responseFile) {
        this.responseFile = responseFile;
        return this;
    }
}
