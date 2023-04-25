package com.xuecheng;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

/**
 * package:  com.xuecheng
 * project_name:  xuecheng-plus
 * 2023/4/23  14:31
 * description:
 *
 * @author wk
 * @version 1.0
 */
public class MyJob implements Job {
    private static int a = 0;

    public static void main(String[] agrs) throws SchedulerException {
        //创建一个Scheduler
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        //创建JobDetail
        JobBuilder jobDetailBuilder = JobBuilder.newJob(MyJob.class);
        jobDetailBuilder.withIdentity("jobName", "jobGroupName");
        JobDetail jobDetail = jobDetailBuilder.build();
        //创建触发的CronTrigger 支持按日历调度
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("triggerName", "triggerGroupName")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule("0/2 * * * * ?"))
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
        scheduler.start();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("myjob-" + a++);
        System.out.println(jobExecutionContext);
        System.out.println(jobExecutionContext.getNextFireTime());

    }
}
