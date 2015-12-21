package goja.core.concurrent;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public interface Callback<T> {

    void onDone(T result, Throwable error) throws Exception;
}
