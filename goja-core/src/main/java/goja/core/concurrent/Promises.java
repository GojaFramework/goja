package goja.core.concurrent;

import goja.core.concurrent.impl.PromiseImpl;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class Promises {

    public static <T> Promise<T> create() {
        return new PromiseImpl<T>();
    }
}
