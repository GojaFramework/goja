package goja.initialize;

import goja.mvc.interceptor.AppInterceptor;
import goja.core.app.GojaConfig;
import goja.core.kits.reflect.ClassPathScanning;
import com.jfinal.plugin.activerecord.Model;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

/**
 * <p> </p>
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.6
 */
public class ClassPathScanningTest {


    @Before
    public void setUp() throws Exception {
        GojaConfig.init();
    }

    @Test
    public void getModels() throws Exception {

        final Set<Class<? extends Model>> models = ClassPathScanning.scan(Model.class);
        for (Class<? extends Model> model : models) {
            System.out.println(model);
        }

    }

    @Test
    public void getAnnotation() throws Exception {


        final Set<Class<?>> interceptors = ClassPathScanning.scanAnnotation(AppInterceptor.class);
        for (Class<?> interceptor : interceptors) {
            System.out.println("interceptor = " + interceptor);
        }
    }
}