package goja.core.concurrent.impl;

import goja.core.G;
import goja.core.concurrent.Future;

import java.util.concurrent.TimeoutException;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class FutureImpl<T> implements Future<T> {

    private volatile boolean done;

    private volatile T result;

    private volatile Throwable error;

    public void setResult(T result) {
        this.result = result;
        done = true;
    }

    public void setError(Throwable error) {
        this.error = error;
        done = true;
    }

    public T get() {
        try {
            return get(Long.MAX_VALUE);
        } catch (TimeoutException e) {
            throw G.notExpected();
        }
    }

    @Override
    public T get(long timeoutMs) throws TimeoutException {
        return get(timeoutMs, 5);
    }

    @Override
    public T get(long timeoutMs, long sleepingIntervalMs) throws TimeoutException {
        long waitingSince = G.time();

        while (!isDone()) {
            if (G.time() - waitingSince > timeoutMs) {
                throw new TimeoutException();
            }

            G.sleep(sleepingIntervalMs);
        }

        if (getError() != null) {
            throw G.rte("Cannot get the result, there was an error!", error);
        }

        return result;
    }

    public Throwable getError() {
        return error;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public boolean isSuccessful() {
        G.must(done, "The promise is not done yet!");
        return error == null;
    }

}
