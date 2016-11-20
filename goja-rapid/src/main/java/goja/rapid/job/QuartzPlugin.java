/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.rapid.job;

import goja.core.annotation.On;
import goja.core.app.GojaConfig;
import goja.core.kits.reflect.ClassPathScanning;
import com.jfinal.plugin.IPlugin;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Set;

import static com.google.common.base.Throwables.propagate;

public class QuartzPlugin implements IPlugin {
    private static final Logger logger = LoggerFactory.getLogger(QuartzPlugin.class);

    private final Scheduler sched;

    /**
     * 定时任务处理.
     */
    public QuartzPlugin() {
        Scheduler tmp_sched = null;
        try {
            tmp_sched = StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            propagate(e);
        }
        this.sched = tmp_sched;
    }

    @Override
    public boolean start() {
        final Set<Class<? extends Job>> jobClazzs = ClassPathScanning.scan(Job.class);

        if (CollectionUtils.isNotEmpty(jobClazzs)) {
            On on;
            for (Class<? extends Job> jobClass : jobClazzs) {
                on = jobClass.getAnnotation(On.class);
                if (on != null) {
                    String jobCronExp = on.value();
                    if (jobCronExp.startsWith("cron.")) {
                        jobCronExp = GojaConfig.getProperty(jobCronExp);
                    }
                    if (on.enabled()) {
                        addJob(jobClass, jobCronExp, jobClass.getName() + ".job");
                    }
                }
            }

            jobClazzs.clear();
        }
        return true;
    }

    private void addJob(Class<? extends Job> jobClass, String jobCronExp, String jobName) {
        if (logger.isDebugEnabled()) {
            logger.debug("GOJA: Initialization job task, job name {} rules {} job'class {}!",
                    jobName, jobCronExp, jobClass);
        }

        JobDetail job = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobName + "group")
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobName, jobName + "group")
                .withSchedule(CronScheduleBuilder.cronSchedule(jobCronExp))
                .startNow()
                .build();

        Date ft = null;
        try {
            ft = sched.scheduleJob(job, trigger);
            sched.start();
        } catch (SchedulerException e) {
            propagate(e);
            logger.error("GOJA::ERRO: JOB {} Error!", jobClass, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(job.getKey()
                    + " has been scheduled to run at: "
                    + ft
                    + " and repeat based on expression: "
                    + jobCronExp);
        }
    }

    @Override
    public boolean stop() {
        try {
            sched.shutdown();
        } catch (SchedulerException e) {
            propagate(e);
        }
        return true;
    }
}
