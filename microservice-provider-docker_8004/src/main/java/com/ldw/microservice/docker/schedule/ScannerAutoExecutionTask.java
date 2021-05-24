package com.ldw.microservice.docker.schedule;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.quartz.JobBuilder.newJob;

/**
 * ClassName:ScannerAutoExecutionTask <br/>
 * Function: 自动执行任务扫描器. <br/>
 * Reason: . <br/>
 * Date: 2020年9月27日 下午5:24:55 <br/>
 *
 * @author ludanwen
 * @see
 * @since JDK 1.8
 */
@Component
@Slf4j
public class ScannerAutoExecutionTask {


    @Autowired
    private Scheduler scheduler;


    /**
     * @desc 执行扫描，每分钟执行一次
     */
//    @Async
//    @Scheduled(cron = "0 */1 * * * ?")
    public void execute() throws ParseException, SchedulerException {
        try {
            log.info("{}----------开始任务清理", DateUtil.now());
            String cronExpress = "0 */1 * * * ?";
            String taskCode = "taskCode_1";
            JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
            jobDetailFactoryBean.setName(taskCode);
            jobDetailFactoryBean.setGroup(Scheduler.DEFAULT_GROUP);

            // TaskJob.class 是任务所要执行操作的类ddd
            jobDetailFactoryBean.setJobClass(Task.class);

            // 任务需要的参数可以通过map方法传递，
            Map<String, Object> map = Maps.newHashMap();
            map.put("taskInfoDTO", "setJobDataMap");
            jobDetailFactoryBean.setJobDataMap(getJobDataMap(map));
            jobDetailFactoryBean.afterPropertiesSet();

            CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
            cronTriggerFactoryBean.setBeanName(taskCode);
            cronTriggerFactoryBean.setCronExpression(cronExpress);
            cronTriggerFactoryBean.setGroup(Scheduler.DEFAULT_GROUP);
            cronTriggerFactoryBean.setName(taskCode);
            cronTriggerFactoryBean.afterPropertiesSet();

            scheduler.scheduleJob(jobDetailFactoryBean.getObject(), cronTriggerFactoryBean.getObject());

            log.info("{}----------结束任务清理", DateUtil.now());
        } catch (Exception e) {
            log.error(" {} 任务检查失败 !", DateUtil.now(), e);
        }

        // 2. 定义一个Trigger，定义该job在4秒后首次执行，并且每隔两秒执行一次
      /*  Date startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2015-09-17 22:40:00");
        startTime.setTime(startTime.getTime());
        SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                .withIdentity("myTrigger", "group1")// 定义名字和组
                .startAt(startTime)//定义开始时间
                .withSchedule(    //定义任务调度的时间间隔和次数
                        SimpleScheduleBuilder
                                .simpleSchedule()
                                .withIntervalInMinutes(5)//定义时间间隔是 5分钟
                                .withRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY)//定义重复执行次数是无限次
                )
                .build();
        //1. 创建一个JodDetail实例 将该实例与Hello job class绑定    (链式写法)
        JobDetail jobDetail = newJob(Task.class) // 定义Job类为HelloQuartz类，这是真正的执行逻辑所在
                .withIdentity("myJob") // 定义name/group
                .build();
        scheduler.scheduleJob(jobDetail, trigger);*/
    }

    /**
     * @param params
     * @return JobDataMap对象
     * @desc 将HashMap转为JobDataMap
     */
    private JobDataMap getJobDataMap(Map<String, Object> params) {
        JobDataMap jdm = new JobDataMap();
        Set<String> keySet = params.keySet();
        Iterator<String> it = keySet.iterator();
        while (it.hasNext()) {
            String key = it.next();
            jdm.put(key, params.get(key));
        }
        return jdm;
    }


    public Boolean interrupt(String taskId) {
        try {
            // tell the scheduler to interrupt our job
            Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(Scheduler.DEFAULT_GROUP));
            triggerKeys.forEach(triggerKey -> {
                log.info(triggerKey.getGroup() + "-" + triggerKey.getName()
                );
            });

            //3. 创建scheduler
            JobDetail jobDetail = scheduler.getJobDetail(JobKey.jobKey(taskId, Scheduler.DEFAULT_GROUP));
//            scheduler.interrupt(jobDetail.getKey());
            scheduler.unscheduleJob(TriggerKey.triggerKey(taskId, Scheduler.DEFAULT_GROUP));
            scheduler.pauseJob(jobDetail.getKey());
            scheduler.pauseTrigger(TriggerKey.triggerKey(taskId, Scheduler.DEFAULT_GROUP));
            scheduler.deleteJob(jobDetail.getKey());
            log.info("{}任务停止成功", taskId);
        } catch (SchedulerException e) {
            log.error("{}任务停止失败", taskId, e);
        }
        return true;
    }


}
