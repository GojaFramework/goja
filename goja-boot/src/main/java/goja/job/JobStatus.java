package goja.job;

import goja.GojaConfig;
import goja.annotation.Every;
import goja.annotation.On;
import goja.annotation.OnApplicationStart;
import goja.kits.base.DateKit;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class JobStatus {

    /**
     * Get the current running state
     *
     * @return Get the current running state
     */
    public static String getStatus() {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        if (JobsPlugin.executor == null) {
            out.println("Jobs execution pool:");
            out.println("~~~~~~~~~~~~~~~~~~~");
            out.println("(not yet started)");
            return sw.toString();
        }
        out.println("Jobs execution pool:");
        out.println("~~~~~~~~~~~~~~~~~~~");
        out.println("Pool size: " + JobsPlugin.executor.getPoolSize());
        out.println("Active count: " + JobsPlugin.executor.getActiveCount());
        out.println("Scheduled task count: " + JobsPlugin.executor.getTaskCount());
        out.println("Queue size: " + JobsPlugin.executor.getQueue().size());
        if (!JobsPlugin.scheduledJobs.isEmpty()) {
            out.println();
            out.println("Scheduled jobs (" + JobsPlugin.scheduledJobs.size() + "):");
            out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~");
            for (Job job : JobsPlugin.scheduledJobs) {
                out.print(job.getClass().getName());
                if (job.getClass().isAnnotationPresent(OnApplicationStart.class) && !(job.getClass().isAnnotationPresent(On.class) || job.getClass().isAnnotationPresent(Every.class))) {
                    OnApplicationStart appStartAnnotation = job.getClass().getAnnotation(OnApplicationStart.class);
                    out.print(" run at application start" + (appStartAnnotation.async() ? " (async)" : "") + ".");
                }

                if (job.getClass().isAnnotationPresent(On.class)) {

                    String cron = job.getClass().getAnnotation(On.class).value();
                    if (cron != null && cron.startsWith("cron.")) {
                        cron = GojaConfig.getProperty(cron);
                    }
                    out.print(" run with cron expression " + cron + ".");
                }
                if (job.getClass().isAnnotationPresent(Every.class)) {
                    out.print(" run every " + job.getClass().getAnnotation(Every.class).value() + ".");
                }
                if (job.lastRun > 0) {
                    out.print(" (last run at " + DateKit.formatDashYMDHMS(new Date(job.lastRun)));
                    if (job.wasError) {
                        out.print(" with error)");
                    } else {
                        out.print(")");
                    }
                } else {
                    out.print(" (has never run)");
                }
                out.println();
            }
        }
        if (!JobsPlugin.executor.getQueue().isEmpty()) {
            out.println();
            out.println("Waiting jobs:");
            out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            BlockingQueue<Runnable> queue = JobsPlugin.executor.getQueue();
            ScheduledFuture[] q = queue.toArray(new ScheduledFuture[queue.size()]);

            for (int i = 0; i < q.length; i++) {
                ScheduledFuture task = q[i];
                out.println(task.toString() + " will run in " + task.getDelay(TimeUnit.SECONDS) + " seconds");
            }
        }
        return sw.toString();
    }
}
