package com.ldw.microservice.docker.schedule;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.stereotype.Component;

import java.util.*;

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
    public void execute() {
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
            cronTriggerFactoryBean.setName("cron_" + taskCode);
            cronTriggerFactoryBean.afterPropertiesSet();

            scheduler.scheduleJob(jobDetailFactoryBean.getObject(), cronTriggerFactoryBean.getObject());

            log.info("{}----------结束任务清理", DateUtil.now());
        } catch (Exception e) {
            log.error(" {} 任务检查失败 !", DateUtil.now(), e);
        }
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
}
