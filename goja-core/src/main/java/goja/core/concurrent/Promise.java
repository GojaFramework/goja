package goja.core.concurrent;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public interface Promise<T> extends Callback<T>, Future<T> {
}
