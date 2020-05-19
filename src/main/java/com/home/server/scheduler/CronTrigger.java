package com.home.server.scheduler;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class CronTrigger {

    public void startScheduler() throws Exception {
        // Job
        JobDetail job = JobBuilder
                .newJob(CheckPlayerCron.class)
                .withIdentity("checkPlayer", "group1")
                .build();

        // Trigger
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("checkPlayer", "group1")
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("0/10 * * * * ?"))
                .build();

        // Start
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(job, trigger);
    }
}
