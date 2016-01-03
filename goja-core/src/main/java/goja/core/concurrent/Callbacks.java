package goja.core.concurrent;

import goja.core.lambda.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class Callbacks {

    private static final Logger logger = LoggerFactory.getLogger(Callbacks.class);

    public static <T> void done(Callback<T> callback, T result, Throwable error) {
        if (callback != null) {
            try {
                callback.onDone(result, error);
            } catch (Exception e) {
                logger.error("Callback error", e);
            }
        }
    }

    public static <T> void success(Callback<T> callback, T result) {
        done(callback, result, null);
    }

    public static <T> void error(Callback<T> callback, Throwable error) {
        done(callback, null, error);
    }

    public static <FROM, TO> Callback<FROM> mapping(final Callback<TO> callback,
                                                    final Mapper<FROM, TO> mapper) {
        return new Callback<FROM>() {

            @Override
            public void onDone(FROM result, Throwable error) throws Exception {
                TO mapped = error == null ? mapper.map(result) : null;
                Callbacks.done(callback, mapped, error);
            }
        };
    }

    public static <T> Callback<T> countDown(final CountDownLatch latch) {
        return new Callback<T>() {
            @Override
            public void onDone(T result, Throwable error) throws Exception {
                latch.countDown();
            }
        };
    }
}
