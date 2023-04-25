package com.xuecheng;

import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * package:  com.xuecheng
 * project_name:  xuecheng-plus
 * 2023/4/23  14:11
 * description: 测试分布式任务调度
 *
 * @author wk
 * @version 1.0
 */
public class TaskSchedule {
    volatile private static int a = 0;

    /**
     *
     */
    public static void main(String[] args) throws SchedulerException {
        //方式1.多线程实现
        final long timeInterval = 1000;
        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {
                    System.out.println("thread-" + a++);
                    try {
                        Thread.sleep(timeInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        //方式2. timer方式实现
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("timer-" + a++);
                //TODO：something
            }
        }, 1000, 2000);  //1秒后开始调度，每2秒执行一次

        //方式3.ScheduledExecutor方式 Java5推出
        ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
        service.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Executors-" + a++);
                        //TODO：something
//                        System.out.println("todo something");
                    }
                }, 1,
                2, TimeUnit.SECONDS);


        /*//方式4.第三方Quartz方式实现
        //创建一个Scheduler
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        //创建JobDetail
        JobBuilder jobDetailBuilder = JobBuilder.newJob(MyJob.class);
        jobDetailBuilder.withIdentity("jobName","jobGroupName");
        JobDetail jobDetail = jobDetailBuilder.build();
        //创建触发的CronTrigger 支持按日历调度
        CronTrigger trigger = (CronTrigger) TriggerBuilder.newTrigger()
                .withIdentity("triggerName", "triggerGroupName")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule("0/2 * * * * ?"))
                .build();
        scheduler.scheduleJob(jobDetail, (Trigger) trigger);
        scheduler.start();*/
    }

    public class MyJob implements Job {
        @Override
        public void execute(JobExecutionContext jobExecutionContext){
            System.out.println("todo something");
        }
    }

    /**
     *
     */
    @Test
    public void multiThread() {
    }

}
