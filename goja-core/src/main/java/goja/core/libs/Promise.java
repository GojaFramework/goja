/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core.libs;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.concurrent.*;

/**
 * <p> . </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-09-11 13:19
 * @since JDK 1.6
 */
@SuppressWarnings("unchecked")
public class Promise<V> implements Future<V>, Action<V> {

    final CountDownLatch taskLock = new CountDownLatch(1);

    boolean invoked = false;

    List<Action<Promise<V>>> callbacks = Lists.newArrayList();

    V result = null;
    Throwable exception = null;

    public static <T> Promise<List<T>> waitAll(final Promise<T>... promises) {
        return waitAll(Arrays.asList(promises));
    }

    public static <T> Promise<List<T>> waitAll(final Collection<Promise<T>> promises) {
        final CountDownLatch waitAllLock = new CountDownLatch(promises.size());
        final Promise<List<T>> result = new Promise<List<T>>() {

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                boolean r = true;
                for (Promise<T> f : promises) {
                    r = r & f.cancel(mayInterruptIfRunning);
                }
                return r;
            }

            @Override
            public boolean isCancelled() {
                boolean r = true;
                for (Promise<T> f : promises) {
                    r = r & f.isCancelled();
                }
                return r;
            }

            @Override
            public boolean isDone() {
                boolean r = true;
                for (Promise<T> f : promises) {
                    r = r & f.isDone();
                }
                return r;
            }

            @Override
            public List<T> get() throws InterruptedException, ExecutionException {
                waitAllLock.await();
                List<T> r = new ArrayList<T>();
                for (Promise<T> f : promises) {
                    r.add(f.get());
                }
                return r;
            }

            @Override
            public List<T> get(long timeout, TimeUnit unit)
                    throws InterruptedException, ExecutionException, TimeoutException {
                waitAllLock.await(timeout, unit);
                return get();
            }
        };
        final Action<Promise<T>> action = new Action<Promise<T>>() {

            public void invoke(Promise<T> completed) {
                waitAllLock.countDown();
                if (waitAllLock.getCount() == 0) {
                    try {
                        result.invoke(result.get());
                    } catch (Exception e) {
                        result.invokeWithException(e);
                    }
                }
            }
        };
        for (Promise<T> f : promises) {
            f.onRedeem(action);
        }
        if (promises.isEmpty()) {
            result.invoke(Collections.<T>emptyList());
        }
        return result;
    }

    public static <T> Promise<T> waitAny(final Promise<T>... futures) {
        final Promise<T> result = new Promise<T>();

        final Action<Promise<T>> action = new Action<Promise<T>>() {

            public void invoke(Promise<T> completed) {
                synchronized (this) {
                    if (result.isDone()) {
                        return;
                    }
                }
                T resultOrNull = completed.getOrNull();
                if (resultOrNull != null) {
                    result.invoke(resultOrNull);
                } else {
                    result.invokeWithException(completed.exception);
                }
            }
        };

        for (Promise<T> f : futures) {
            f.onRedeem(action);
        }

        return result;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        return invoked;
    }

    public V getOrNull() {
        return result;
    }

    public V get() throws InterruptedException, ExecutionException {
        taskLock.await();
        if (exception != null) {
            // The result of the promise is an exception - throw it
            throw new ExecutionException(exception);
        }
        return result;
    }

    public V get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        taskLock.await(timeout, unit);
        if (exception != null) {
            // The result of the promise is an exception - throw it
            throw new ExecutionException(exception);
        }
        return result;
    }

    public void invoke(V result) {
        invokeWithResultOrException(result, null);
    }

    public void invokeWithException(Throwable t) {
        invokeWithResultOrException(null, t);
    }

    protected void invokeWithResultOrException(V result, Throwable t) {
        synchronized (this) {
            if (!invoked) {
                invoked = true;
                this.result = result;
                this.exception = t;
                taskLock.countDown();
            } else {
                return;
            }
        }
        for (Action<Promise<V>> callback : callbacks) {
            callback.invoke(this);
        }
    }

    public void onRedeem(Action<Promise<V>> callback) {
        synchronized (this) {
            if (!invoked) {
                callbacks.add(callback);
            }
        }
        if (invoked) {
            callback.invoke(this);
        }
    }
}
