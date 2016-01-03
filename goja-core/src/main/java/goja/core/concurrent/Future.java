package goja.core.concurrent;

import java.util.concurrent.TimeoutException;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public interface Future<T> {

    T get();

    T get(long timeoutMs) throws TimeoutException;

    T get(long timeoutMs, long sleepingIntervalMs) throws TimeoutException;

    boolean isDone();

    boolean isSuccessful();
}
