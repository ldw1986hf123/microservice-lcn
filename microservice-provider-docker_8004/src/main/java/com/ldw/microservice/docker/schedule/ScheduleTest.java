package com.ldw.microservice.docker.schedule;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
public class ScheduleTest {


    @Autowired
    private Scheduler scheduler;


    /**
     * @desc 执行扫描，每分钟执行一次
     */
    @Async
    @Scheduled(cron = "0 */1 * * * ?")
    public void execute() {
        try {
            Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(Scheduler.DEFAULT_GROUP));
            for (TriggerKey key : triggerKeys) {
                System.out.println(key );
            }


        } catch (SchedulerException e) {
            e.printStackTrace();
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
