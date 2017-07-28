package com.geecommerce.core.cron;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.SchedulerException;

import com.geecommerce.core.App;
import com.geecommerce.core.mail.Mailer;
import com.geecommerce.core.mail.SMTPMailer;

public class TaskListener implements JobListener {
    private String name;

    private static final Logger log = LogManager.getLogger(TaskListener.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public TaskListener(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        Environment.init();

        try {
            log.info("=============================================================================================");
            log.info("Starting task '" + context.getJobDetail().getKey() + "' at " + dateFormat.format(new Date())
                + ".");
            log.info("---------------------------------------------------------------------------------------------");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        try {
            log.info("---------------------------------------------------------------------------------------------");
            log.info("Task '" + context.getJobDetail().getKey() + "' was vetoed at " + dateFormat.format(new Date())
                + ".");
            log.info("---------------------------------------------------------------------------------------------");

//            sendMail(
//                "Task '" + context.getJobDetail().getKey()
//                    + "' has been vetoed",
//                "Task '" + context.getJobDetail().getKey() + "' has been vetoed. "
//                    + (context.getNextFireTime() == null ? ""
//                        : "Next trigger fire time is at " + dateFormat.format(context.getNextFireTime())
//                            + "."),
//                context, true);
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            Environment.cleanUp();
        }
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        JobKey jobKey = Tasks.getSingleTaskToRun();

        if (jobKey != null) {
            System.out.println("Completed task " + jobKey + (jobException == null ? "." : " with errors."));

            try {
                context.getScheduler().shutdown();
            } catch (SchedulerException e) {
                e.printStackTrace();
            } finally {
                Environment.cleanUp();
                System.exit(jobException == null ? 0 : 1);
            }
        } else {
            try {
                if (jobException == null) {
                    log.info(
                        "---------------------------------------------------------------------------------------------");
                    log.info("Task '" + context.getJobDetail().getKey() + "' completed at "
                        + dateFormat.format(new Date()) + ". "
                        + (context.getNextFireTime() == null ? ""
                            : "Next trigger fire time is at " + dateFormat.format(context.getNextFireTime())
                                + "."));
                    log.info(
                        "=============================================================================================");

//                    sendMail("Task '" + context.getJobDetail().getKey() + "' completed",
//                        "Task '" + context.getJobDetail().getKey() + "' has completed. "
//                            + (context.getNextFireTime() == null ? ""
//                                : "Next trigger fire time is at "
//                                    + dateFormat.format(context.getNextFireTime()) + "."),
//                        context, false);
                } else {
                    log.error(
                        "---------------------------------------------------------------------------------------------");
                    log.error("Task '" + context.getJobDetail().getKey() + "' threw an exception at "
                        + dateFormat.format(new Date()) + ". "
                        + (context.getNextFireTime() == null ? ""
                            : "Next trigger fire time is at " + dateFormat.format(context.getNextFireTime())
                                + " (unless this exception prevents next trigger to execute)."));
                    log.error(jobException.getMessage() + "\n\n", jobException, true);
                    log.error(
                        "=============================================================================================");

                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    jobException.printStackTrace(pw);

                    System.out.println(sw.toString());

//                    sendMail("Task '" + context.getJobDetail().getKey() + "' threw an error: "
//                        + jobException.getMessage(), sw.toString(), context, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Environment.cleanUp();
            }
        }
    }

    private void sendMail(String subject, String body, JobExecutionContext context, boolean isError) {
        List<String> receipients = null;

        Class<?> clazz = context.getJobInstance().getClass();

        App app = App.get();

        if (isError) {
            receipients = app.cpStrList_("general/cron/" + clazz.getName() + "/notification/error");

            if (receipients == null)
                receipients = app.cpStrList_("general/cron/notification/error");
        } else {
            receipients = app.cpStrList_("general/cron/" + clazz.getName() + "/notification/success");

            if (receipients == null)
                receipients = app.cpStrList_("general/cron/notification/success");
        }

        if (receipients != null && receipients.size() > 0) {
            Mailer mailer = new SMTPMailer();

            for (String receipient : receipients) {
                mailer.send(subject, null, body, receipient, null, (List<URL>) null, (List<URL>) null);
            }
        }
    }
}
