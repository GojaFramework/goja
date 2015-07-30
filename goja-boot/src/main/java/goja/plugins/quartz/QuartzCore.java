/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 jfinal app. jfapp Group.
 */

package goja.plugins.quartz;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;
import java.util.Properties;

import static com.google.common.base.Throwables.propagate;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.impl.StdSchedulerFactory.PROP_JOB_STORE_CLASS;
import static org.quartz.impl.StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME;
import static org.quartz.impl.StdSchedulerFactory.PROP_THREAD_POOL_CLASS;

/**
 * <p>
 * .
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-06-23 14:16
 * @since JDK 1.6
 */
public class QuartzCore {

    private final Scheduler sched;


    public QuartzCore() {
        Scheduler tmp_sched = null;

        Properties job_config = new Properties();
        job_config.setProperty(PROP_JOB_STORE_CLASS, LocalJobStore.class.getName());
        job_config.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        job_config.setProperty("org.quartz.jobStore.tablePrefix", "QGJ_");
        job_config.setProperty("org.quartz.jobStore.isClustered", "false");
        job_config.setProperty("org.quartz.jobStore.maxMisfiresToHandleAtATime", "1");
        job_config.setProperty("org.quartz.jobStore.misfireThreshold", "60000");

        job_config.setProperty(PROP_THREAD_POOL_CLASS, "org.quartz.simpl.SimpleThreadPool");
        job_config.setProperty("org.quartz.threadPool.threadCount", "10");
        job_config.setProperty("org.quartz.threadPool.threadPriority", "5");
        job_config.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");


        job_config.setProperty(PROP_SCHED_INSTANCE_NAME, "DefaultQuartzScheduler");
        job_config.setProperty("org.quartz.scheduler.rmi.export ", "false");
        job_config.setProperty("org.quartz.scheduler.rmi.proxy ", "false");
        job_config.setProperty("org.quartz.scheduler.wrapJobExecutionInUserTransaction ", "false");

        try {
            tmp_sched = new StdSchedulerFactory(job_config).getScheduler();
        } catch (SchedulerException e) {
            propagate(e);
        }
        this.sched = tmp_sched;
    }


    public void start() {
        //        addJob(AJob.class, "* * * * * ?", "test job");
    }


    private void addJob(Class<? extends Job> jobClass, String jobCronExp, String jobName) {
        JobDetail job = newJob(jobClass)
                .withIdentity(jobName, jobName + "goja.group")
                .build();
        Trigger trigger = newTrigger()
                .withIdentity(jobName, jobName + "goja.group")
                .withSchedule(cronSchedule(jobCronExp))
                .startNow()
                .build();

        Date ft = null;
        try {
            ft = sched.scheduleJob(job, trigger);
            sched.start();
        } catch (SchedulerException e) {
            propagate(e);
        }
    }
}
