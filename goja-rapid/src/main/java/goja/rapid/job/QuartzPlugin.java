/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.rapid.job;

import com.jfinal.plugin.IPlugin;
import goja.core.annotation.On;
import goja.core.app.GojaConfig;
import java.util.Date;
import java.util.List;
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

import static com.google.common.base.Throwables.propagate;

public class QuartzPlugin implements IPlugin {
  private static final Logger logger = LoggerFactory.getLogger(QuartzPlugin.class);
  private final Scheduler sched;
  private final List<Class> jobClasses;

  /**
   * 定时任务处理.
   *
   * @param jobClasses 任务class
   */
  public QuartzPlugin(List<Class> jobClasses) {
    Scheduler tmp_sched = null;
    try {
      tmp_sched = StdSchedulerFactory.getDefaultScheduler();
    } catch (SchedulerException e) {
      propagate(e);
    }
    this.sched = tmp_sched;
    this.jobClasses = jobClasses;
  }

  @Override
  public boolean start() {
    if (jobClasses != null && !jobClasses.isEmpty()) {
      On on;
      for (Class jobClass : jobClasses) {
        on = (On) jobClass.getAnnotation(On.class);
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

      jobClasses.clear();
    }
    return true;
  }

  private void addJob(Class<? extends Job> jobClass, String jobCronExp, String jobName) {
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
