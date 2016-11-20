/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.job;

import goja.core.annotation.Every;
import goja.core.annotation.On;
import goja.core.annotation.OnApplicationStart;
import goja.core.annotation.OnApplicationStop;
import goja.core.app.GojaConfig;
import goja.core.app.GojaPropConst;
import goja.core.exceptions.GojaException;
import goja.core.exceptions.UnexpectedException;
import goja.core.kits.reflect.ClassPathScanning;
import goja.core.libs.Expression;
import goja.core.libs.PThreadFactory;
import goja.core.libs.Time;
import com.jfinal.plugin.IPlugin;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JobsPlugin implements IPlugin {

    private static final Logger logger = LoggerFactory.getLogger(JobsPlugin.class);
    static ScheduledThreadPoolExecutor executor;
    private static List<Job> scheduledJobs = null;

    private final List<Class>  applicationStopJobs  = Lists.newArrayList();
    private final List<Job<?>> applicationStartJobs = Lists.newArrayList();

    public JobsPlugin() {

        int            corePoolSize  = GojaConfig.getPropertyToInt(GojaPropConst.APP_JOB_POOL, 10);
        PThreadFactory threadFactory = new PThreadFactory("goja-jobs");
        executor = new ScheduledThreadPoolExecutor(corePoolSize, threadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }

    static <V> void scheduleForCRON(Job<V> job) {
        if (!job.getClass().isAnnotationPresent(On.class)) {
            return;
        }
        String cron = job.getClass().getAnnotation(On.class).value();
        if (cron.startsWith("cron.")) {
            cron = GojaConfig.getProperty(cron);
        }
        final Object eval = Expression.evaluate(cron, cron);
        if (eval == null) {
            logger.error("the jon cron is null.");
            return;
        }
        cron = eval.toString();
        if (Strings.isNullOrEmpty(cron) || "never".equalsIgnoreCase(cron)) {
            logger.info("Skipping job %s, cron expression is not defined", job.getClass().getName());
            return;
        }
        try {
            Date now = new Date();
            cron = eval.toString();
            Time.CronExpression cronExp  = new Time.CronExpression(cron);
            Date                nextDate = cronExp.getNextValidTimeAfter(now);
            if (nextDate == null) {
                logger.warn(
                        "The cron expression for job %s doesn't have any match in the future, will never be executed",
                        job.getClass().getName());
                return;
            }
            if (nextDate.equals(job.nextPlannedExecution)) {
                // Bug #13: avoid running the job twice for the same time
                // (happens when we end up running the job a few minutes before the planned time)
                Date nextInvalid = cronExp.getNextInvalidTimeAfter(nextDate);
                nextDate = cronExp.getNextValidTimeAfter(nextInvalid);
            }
            job.nextPlannedExecution = nextDate;
            executor.schedule((Callable<V>) job, nextDate.getTime() - now.getTime(),
                    TimeUnit.MILLISECONDS);
            job.executor = executor;
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    public List<Class> getApplicationStopJobs() {
        return applicationStopJobs;
    }

    public List<Job<?>> getApplicationStartJobs() {
        return applicationStartJobs;
    }

    @Override
    public boolean start() {
        // fixed: If the configuration to start the JOB, but there is no JOB class, not to start.
        final Set<Class<? extends Job>> jobCLasses = ClassPathScanning.scan(Job.class);
        if (CollectionUtils.isEmpty(jobCLasses)) {
            return true;
        } else {

            scheduledJobs = Lists.newArrayList();

            for (Class<? extends Job> clazz : jobCLasses) {
                // @OnApplicationStart
                if (clazz.isAnnotationPresent(OnApplicationStart.class)) {
                    try {
                        Job<?> job = ((Job<?>) clazz.newInstance());
                        applicationStartJobs.add(job);
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new UnexpectedException("Job could not be instantiated", e);
                    } catch (Throwable ex) {
                        if (ex instanceof GojaException) {
                            throw (GojaException) ex;
                        }
                        throw new UnexpectedException(ex);
                    }
                }
                // @OnApplicationStop
                if (clazz.isAnnotationPresent(OnApplicationStop.class)) {
                    applicationStopJobs.add(clazz);
                }
                // @On
                if (clazz.isAnnotationPresent(On.class)) {
                    try {
                        Job<?> job = ((Job<?>) clazz.newInstance());
                        scheduledJobs.add(job);
                        scheduleForCRON(job);
                    } catch (InstantiationException | IllegalAccessException ex) {
                        throw new UnexpectedException("Cannot instanciate Job " + clazz.getName());
                    }
                }
                // @Every
                if (clazz.isAnnotationPresent(Every.class)) {
                    try {
                        Job job = clazz.newInstance();
                        scheduledJobs.add(job);
                        String value = job.getClass().getAnnotation(Every.class).value();
                        if (value.startsWith("cron.")) {
                            value = GojaConfig.getProperty(value);
                        }
                        value = Expression.evaluate(value, value).toString();
                        if (!"never".equalsIgnoreCase(value)) {
                            executor.scheduleWithFixedDelay(job, Time.parseDuration(value),
                                    Time.parseDuration(value), TimeUnit.SECONDS);
                        }
                    } catch (InstantiationException | IllegalAccessException ex) {
                        throw new UnexpectedException("Cannot instanciate Job " + clazz.getName());
                    }
                }
            }

        }

        return true;
    }

    @Override
    public boolean stop() {

        if (scheduledJobs != null) {
            scheduledJobs.clear();
            scheduledJobs = null;
        }
        executor.shutdownNow();
        executor.getQueue().clear();
        return true;
    }


    public void clear() {
        applicationStartJobs.clear();
        applicationStopJobs.clear();
    }
}
