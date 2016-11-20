package com.geecommerce.core.cron;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.simpl.PropertySettingJobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geecommerce.core.App;

public class GuicePropertySettingJobFactory extends PropertySettingJobFactory {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        JobDetail jobDetail = bundle.getJobDetail();
        Class<? extends Job> jobClass = jobDetail.getJobClass();

        try {
            if (App.get().context() == null) {
                Environment.init();
            }

            if (log.isDebugEnabled()) {
                log.debug("Producing instance of Job '" + jobDetail.getKey() + "', class=" + jobClass.getName());
            }

            Job job = (Job) App.get().inject(jobClass);

            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.putAll(scheduler.getContext());
            jobDataMap.putAll(bundle.getJobDetail().getJobDataMap());
            jobDataMap.putAll(bundle.getTrigger().getJobDataMap());

            setBeanProps(job, jobDataMap);

            return job;
        } catch (Exception e) {
            System.out.println(Thread.currentThread().getName() + " ----------- " + App.get().context());

            SchedulerException se = new SchedulerException(
                "Problem instantiating class '" + jobDetail.getJobClass().getName() + "'", e);
            throw se;
        }
    }
}
