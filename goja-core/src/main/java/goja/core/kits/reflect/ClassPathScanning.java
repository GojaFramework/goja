package goja.core.kits.reflect;

import goja.core.app.GojaConfig;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * <p> </p>
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.6
 */
public class ClassPathScanning {

    private static final List<Reflections> reflectionsList = Lists.newArrayList();

    static {
        final List<String> appPacks = GojaConfig.getAppScans();
        for (String appPack : appPacks) {

            final Reflections reflections = new Reflections(appPack,
                    new TypeAnnotationsScanner(), new SubTypesScanner(), new ResourcesScanner());
            reflectionsList.add(reflections);
        }
    }

    public static <T> Set<Class<? extends T>> scan(final Class<T> type) {

        final Set<Class<? extends T>> clazzs = Sets.newHashSet();

        for (Reflections reflections : reflectionsList) {
            final Set<Class<? extends T>> packClazzs = reflections.getSubTypesOf(type);
            clazzs.addAll(packClazzs);
        }

        return clazzs;
    }

    public static Set<Class<?>> scanAnnotation(final Class<? extends Annotation> annotation) {

        final Set<Class<?>> clazzs = Sets.newHashSet();

        for (Reflections reflections : reflectionsList) {
            final Set<Class<?>> annotations = reflections.getTypesAnnotatedWith(annotation);
            clazzs.addAll(annotations);
        }

        return clazzs;
    }

    public static Set<String> scanResources(final Predicate<String> namePredicate) {

        final Set<String> appResources = Sets.newHashSet();

        for (Reflections reflections : reflectionsList) {
            final Set<String>   resources   = reflections.getResources(namePredicate);
            appResources.addAll(resources);
        }

        return appResources;
    }

    public static Set<String> scanResources(final Pattern pattern) {

        final Set<String> appResources = Sets.newHashSet();

        for (Reflections reflections : reflectionsList) {
            final Set<String>   resources   = reflections.getResources(pattern);
            appResources.addAll(resources);
        }

        return appResources;
    }
}
