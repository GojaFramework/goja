package goja.core.concurrent.impl;

import goja.core.concurrent.Promise;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class PromiseImpl<T> extends FutureImpl<T> implements Promise<T> {

    @Override
    public void onDone(T result, Throwable error) {
        if (error != null) {
            setError(error);
        } else {
            setResult(result);
        }
    }

}
