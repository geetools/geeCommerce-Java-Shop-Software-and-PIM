package com.geecommerce.core.cron;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.geecommerce.core.App;
import com.geecommerce.core.config.SystemConfig;
import com.geecommerce.core.service.annotation.Task;

public class TaskRunner {
    public static void main(String[] args) throws SchedulerException {
        System.getenv().forEach((k,v) -> System.out.println("[Env]  " + k + ": " + v));
        System.getProperties().forEach((k,v) -> System.out.println("[Prop] " + k + ": " + v));
        
        System.out.println("Webapp path: " + SystemConfig.GET.val(SystemConfig.APPLICATION_WEBAPP_PATH));
        System.out.println("Projects path: " + SystemConfig.GET.val(SystemConfig.APPLICATION_PROJECTS_PATH));
        
        Environment.init();

        Logger log = LogManager.getLogger(TaskRunner.class);

        List<Taskable> tasks = Tasks.find();

        log.info("Found tasks: " + tasks);

        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler scheduler = sf.getScheduler();
        scheduler.getListenerManager().addJobListener(new TaskListener("Task-Listener"));

        scheduler.clear();

        scheduler.start();

        JobKey jobKey = Tasks.getSingleTaskToRun();

        try {
            for (Taskable taskable : tasks) {
                Class<?> clazz = taskable.getClass();
                Task taskAnnotation = clazz.getAnnotation(Task.class);

                if (jobKey != null) {
                    if (!jobKey.getGroup().equals(taskAnnotation) && !jobKey.getName().equals(taskAnnotation.name()))
                        continue;

                    try {
                        Job job = (Job) taskable;

                        System.out.println("Running task " + jobKey + " ... ");

                        JobDetail jobDetail = newJob(job.getClass()).withIdentity(jobKey).storeDurably().build();
                        scheduler.addJob(jobDetail, true);
                        scheduler.triggerJob(jobKey);
                    } catch (Throwable t) {
                        System.out.println("Task " + taskAnnotation.name() + "@" + taskAnnotation.group()
                            + " threw an error: " + t.getMessage());
                        t.printStackTrace();
                        log.error("Task " + taskAnnotation.name() + "@" + taskAnnotation.group() + " threw an error: "
                            + t.getMessage(), t);
                    }
                } else {
                    App app = App.get();
                    boolean enabled = app.cpBool_("general/cron/" + clazz.getName() + "/enabled",
                        taskAnnotation.enabled());
                    String schedule = app.cpStr_("general/cron/" + clazz.getName() + "/schedule",
                        taskAnnotation.schedule());

                    if (!enabled)
                        continue;

                    try {
                        Job job = (Job) taskable;

                        log.info("Initializing task: [name=" + taskAnnotation.name() + ", group="
                            + taskAnnotation.group() + ", schedule=" + taskAnnotation.schedule() + ", transaction="
                            + taskAnnotation.transaction() + "].");

                        JobDetail jobDetail = newJob(job.getClass())
                            .withIdentity(taskAnnotation.name(), taskAnnotation.group())
                            .requestRecovery(taskAnnotation.recoverable()).build();

                        CronScheduleBuilder csb = cronSchedule(schedule);

                        switch (taskAnnotation.onMisfire()) {
                        case DO_NOTHING:
                            csb.withMisfireHandlingInstructionDoNothing();
                            break;
                        case RETRY_ALL:
                            csb.withMisfireHandlingInstructionIgnoreMisfires();
                            break;
                        case RETRY_ONE:
                            csb.withMisfireHandlingInstructionFireAndProceed();
                            break;
                        default:
                            csb.withMisfireHandlingInstructionDoNothing();
                            break;
                        }

                        CronTrigger trigger = newTrigger()
                            .withIdentity(taskAnnotation.name() + " Trigger", taskAnnotation.group())
                            .withSchedule(csb).withPriority(taskAnnotation.priority()).startNow().build();

                        scheduler.scheduleJob(jobDetail, trigger);

                        log.info("Found job: " + taskable.getClass() + " [nextFireTime=" + trigger.getNextFireTime()
                            + ", isShutdown=" + scheduler.isShutdown() + ", isInStandbyMode="
                            + scheduler.isInStandbyMode() + "].");
                    } catch (Throwable t) {
                        System.out.println("Task " + taskAnnotation.name() + "@" + taskAnnotation.group()
                            + " threw an error: " + t.getMessage());
                        t.printStackTrace();
                        log.error("Task " + taskAnnotation.name() + "@" + taskAnnotation.group() + " threw an error: "
                            + t.getMessage(), t);
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            log.error(t.getMessage(), t);
            scheduler.shutdown();
        }
    }
}
