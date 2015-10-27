/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja;

import goja.core.app.ApplicationMode;
import goja.core.app.GojaConfig;
import goja.core.exceptions.GojaException;
import goja.core.exceptions.UnexpectedException;
import goja.core.libs.Action;
import goja.core.libs.PThreadFactory;
import goja.core.libs.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * <p> . </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-06-24 22:05
 * @since JDK 1.6
 */
@SuppressWarnings("UnusedDeclaration")
public final class Invoker {

    private static final Logger logger = LoggerFactory.getLogger(Invoker.class);
    /**
     * Main executor for requests invocations.
     */
    public static ScheduledThreadPoolExecutor executor = null;

    private Invoker() {
    }

    /**
     * Run the code in a new thread took from a thread pool.
     *
     * @param invocation The code to run
     * @return The future object, to know when the task is completed
     */
    public static Future<?> invoke(final Invocation invocation) {
        return executor.submit(invocation);
    }

    /**
     * Run the code in a new thread after a delay
     *
     * @param invocation The code to run
     * @param millis     The time to wait before, in milliseconds
     * @return The future object, to know when the task is completed
     */
    public static Future<?> invoke(final Invocation invocation, long millis) {
        return executor.schedule(invocation, millis, TimeUnit.MILLISECONDS);
    }

    /**
     * Run the code in the same thread than caller.
     *
     * @param invocation The code to run
     */
    public static void invokeInThread(DirectInvocation invocation) {
        boolean retry = true;
        while (retry) {
            invocation.run();
            if (invocation.retry == null) {
                retry = false;
            } else {
                try {
                    if (invocation.retry.task != null) {
                        invocation.retry.task.get();
                    } else {
                        Thread.sleep(invocation.retry.timeout);
                    }
                } catch (Exception e) {
                    throw new UnexpectedException(e);
                }
                retry = true;
            }
        }
    }

    /**
     * The class/method that will be invoked by the current operation
     */
    public static class InvocationContext {

        public static ThreadLocal<InvocationContext> current = new ThreadLocal<InvocationContext>();
        private final List<Annotation> annotations;
        private final String           invocationType;

        public InvocationContext(String invocationType) {
            this.invocationType = invocationType;
            this.annotations = new ArrayList<Annotation>();
        }

        public InvocationContext(String invocationType, List<Annotation> annotations) {
            this.invocationType = invocationType;
            this.annotations = annotations;
        }

        public InvocationContext(String invocationType, Annotation[] annotations) {
            this.invocationType = invocationType;
            this.annotations = Arrays.asList(annotations);
        }

        public InvocationContext(String invocationType, Annotation[]... annotations) {
            this.invocationType = invocationType;
            this.annotations = new ArrayList<Annotation>();
            for (Annotation[] some : annotations) {
                this.annotations.addAll(Arrays.asList(some));
            }
        }

        public static InvocationContext current() {
            return current.get();
        }

        public List<Annotation> getAnnotations() {
            return annotations;
        }

        @SuppressWarnings("unchecked")
        public <T extends Annotation> T getAnnotation(Class<T> clazz) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().isAssignableFrom(clazz)) {
                    return (T) annotation;
                }
            }
            return null;
        }

        public <T extends Annotation> boolean isAnnotationPresent(Class<T> clazz) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().isAssignableFrom(clazz)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Returns the InvocationType for this invocation - Ie: A plugin can use this to find out if it runs in the
         * context of a background Job
         */
        public String getInvocationType() {
            return invocationType;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("InvocationType: ");
            builder.append(invocationType);
            builder.append(". annotations: ");
            for (Annotation annotation : annotations) {
                builder.append(annotation.toString()).append(",");
            }
            return builder.toString();
        }
    }

    /**
     * An Invocation in something to run in a Play! context
     */
    public static abstract class Invocation implements Runnable {

        /**
         * Override this method
         *
         * @throws Exception
         */
        public abstract void execute() throws Exception;


        /**
         * Needs this method to do stuff *before* init() is executed. The different Invocation-implementations does a
         * lot of stuff in init() and they might do it before calling super.init()
         */
        protected void preInit() {
            // clear language for this request - we're resolving it later when it is needed
//            I18n.clear();
        }

        /**
         * Init the call (especially usefull in DEV mode to detect changes)
         */
        public boolean init() {
            Thread.currentThread().setContextClassLoader(Goja.class.getClassLoader());
            if (!Goja.started) {
                if ( GojaConfig.getApplicationMode() == ApplicationMode.PROD) {
                    throw new UnexpectedException("Application is not started");
                }
            }
            InvocationContext.current.set(getInvocationContext());
            return true;
        }


        public abstract InvocationContext getInvocationContext();

        /**
         * Things to do before an Invocation
         */
        public void before() {
            Thread.currentThread().setContextClassLoader(Goja.class.getClassLoader());
        }

        /**
         * Things to do after an Invocation. (if the Invocation code has not thrown any exception)
         */
        public void after() {
        }

        /**
         * Things to do when the whole invocation has succeeded (before + execute + after)
         */
        public void onSuccess() throws Exception {
        }

        /**
         * Things to do if the Invocation code thrown an exception
         */
        public void onException(Throwable e) {
            if (e instanceof GojaException) {
                throw (GojaException) e;
            }
            throw new UnexpectedException(e);
        }

        /**
         * The request is suspended
         *
         * @param suspendRequest
         */
        public void suspend(Suspend suspendRequest) {
            if (suspendRequest.task != null) {
                WaitForTasksCompletion.waitFor(suspendRequest.task, this);
            } else {
                Invoker.invoke(this, suspendRequest.timeout);
            }
        }

        /**
         * Things to do in all cases after the invocation.
         */
        public void _finally() {
            InvocationContext.current.remove();
        }

        /**
         * It's time to execute.
         */
        public void run() {
            try {
                preInit();
                if (init()) {
                    before();
                    execute();
                    after();
                    onSuccess();
                }
            } catch (Suspend e) {
                suspend(e);
                after();
            } catch (Throwable e) {
                onException(e);
            } finally {
                _finally();
            }
        }
    }

    /**
     * A direct invocation (in the same thread than caller)
     */
    public static abstract class DirectInvocation extends Invocation {

        public static final String invocationType = "DirectInvocation";

        Suspend retry = null;

        @Override
        public boolean init() {
            retry = null;
            return super.init();
        }

        @Override
        public void suspend(Suspend suspendRequest) {
            retry = suspendRequest;
        }

        @Override
        public InvocationContext getInvocationContext() {
            return new InvocationContext(invocationType);
        }
    }

    /**
     * Init executor at load time.
     */
    static {
        int core = GojaConfig.getPropertyToInt("goja.pool",  GojaConfig.getApplicationMode().isDev() ? 1 : (Runtime.getRuntime().availableProcessors() + 1));
        executor = new ScheduledThreadPoolExecutor(core, new PThreadFactory("goja"), new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * Throwable to indicate that the request must be suspended
     */
    @SuppressWarnings("UnusedDeclaration")
    public static class Suspend extends GojaException {

        private static final long serialVersionUID = 4876949569944002423L;
        /**
         * Suspend for a timeout (in milliseconds).
         */
        long timeout;

        /**
         * Wait for task execution.
         */
        Future<?> task;

        public Suspend(long timeout) {
            this.timeout = timeout;
        }

        public Suspend(Future<?> task) {
            this.task = task;
        }

        @Override
        public String getErrorTitle() {
            return "Request is suspended";
        }

        @Override
        public String getErrorDescription() {
            if (task != null) {
                return "Wait for " + task;
            }
            return "Retry in " + timeout + " ms.";
        }
    }

    /**
     * Utility that track tasks completion in order to resume suspended requests.
     */
    static class WaitForTasksCompletion extends Thread {

        static WaitForTasksCompletion instance;
        Map<Future<?>, Invocation> queue;

        public WaitForTasksCompletion() {
            queue = new ConcurrentHashMap<Future<?>, Invocation>();
            setName("WaitForTasksCompletion");
            setDaemon(true);
        }

        public static <V> void waitFor(Future<V> task, final Invocation invocation) {
            if (task instanceof Promise) {
                Promise<V> smartFuture = (Promise<V>) task;
                smartFuture.onRedeem(new Action<Promise<V>>() {
                    public void invoke(Promise<V> result) {
                        executor.submit(invocation);
                    }
                });
            } else {
                synchronized (WaitForTasksCompletion.class) {
                    if (instance == null) {
                        instance = new WaitForTasksCompletion();
                        logger.warn("Start WaitForTasksCompletion");
                        instance.start();
                    }
                    instance.queue.put(task, invocation);
                }
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (!queue.isEmpty()) {
                        for (Future<?> task : new HashSet<Future<?>>(queue.keySet())) {
                            if (task.isDone()) {
                                executor.submit(queue.get(task));
                                queue.remove(task);
                            }
                        }
                    }
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    logger.warn("While waiting for task completions", ex);
                }
            }
        }
    }
}
