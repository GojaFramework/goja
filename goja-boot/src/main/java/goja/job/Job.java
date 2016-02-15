/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.job;

import goja.Invoker;
import goja.core.libs.Promise;
import goja.core.libs.Time;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.LoggerFactory;

/**
 * A job is an asynchronously executed unit of work
 *
 * @param <V> The job result type (if any)
 */
public class Job<V> extends Invoker.Invocation implements Callable<V> {

  public static final String invocationType = "GojaJob";
  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Job.class);
  protected ExecutorService executor;

  protected long lastRun = 0;
  protected boolean wasError = false;

  protected Throwable lastException = null;

  Date nextPlannedExecution = null;

  @Override
  public Invoker.InvocationContext getInvocationContext() {
    return new Invoker.InvocationContext(invocationType, this.getClass().getAnnotations());
  }

  /**
   * Here you do the job
   */
  public void doJob() throws Exception {
  }

  /**
   * Here you do the job and return a result
   */
  public V doJobWithResult() throws Exception {
    doJob();
    return null;
  }

  @Override
  public void execute() throws Exception {
  }

  /**
   * Start this job now (well ASAP)
   *
   * @return the job completion
   */
  public Promise<V> now() {
    final Promise<V> smartFuture = new Promise<V>();
    JobsPlugin.executor.submit(getJobCallingCallable(smartFuture));
    return smartFuture;
  }

  /**
   * Start this job in several seconds
   *
   * @return the job completion
   */
  public Promise<V> in(String delay) {
    return in(Time.parseDuration(delay));
  }

  /**
   * Start this job in several seconds
   *
   * @return the job completion
   */
  public Promise<V> in(int seconds) {
    final Promise<V> smartFuture = new Promise<V>();

    JobsPlugin.executor.schedule(getJobCallingCallable(smartFuture), seconds, TimeUnit.SECONDS);

    return smartFuture;
  }

  private Callable<V> getJobCallingCallable(final Promise<V> smartFuture) {
    return new Callable<V>() {
      public V call() throws Exception {
        try {
          V result = Job.this.call();
          if (smartFuture != null) {
            smartFuture.invoke(result);
          }
          return result;
        } catch (Exception e) {
          if (smartFuture != null) {
            smartFuture.invokeWithException(e);
          }
          return null;
        }
      }
    };
  }

  /**
   * Run this job every n seconds
   */
  public void every(String delay) {
    every(Time.parseDuration(delay));
  }

  /**
   * Run this job every n seconds
   */
  public void every(int seconds) {
    JobsPlugin.executor.scheduleWithFixedDelay(this, seconds, seconds, TimeUnit.SECONDS);
  }

  // Customize Invocation
  @Override
  public void onException(Throwable e) {
    wasError = true;
    lastException = e;
    try {
      super.onException(e);
    } catch (Throwable ex) {
      logger.error("Error during job execution (%s)", this, ex);
    }
  }

  @Override
  public void run() {
    call();
  }

  @Override
  public V call() {
    try {
      if (init()) {
        before();
        V result;

        lastException = null;
        lastRun = System.currentTimeMillis();
        result = doJobWithResult();
        wasError = false;
        after();
        return result;
      }
    } catch (Throwable e) {
      onException(e);
    } finally {
      _finally();
    }
    return null;
  }

  @Override
  public void _finally() {
    super._finally();
    if (executor == JobsPlugin.executor) {
      JobsPlugin.scheduleForCRON(this);
    }
  }

  @Override
  public String toString() {
    return this.getClass().getName();
  }
}
