package com.ldw.microservice.docker.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldw.microservice.docker.eo.TaskInfoDO;
import com.ldw.microservice.docker.mapper.TaskInfoMapper;
import com.ldw.microservice.docker.schedule.ScannerAutoExecutionTask;
import com.ldw.microservice.docker.service.TaskInfoService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ldw
 * @since 2021-03-18
 */
@Service
@Slf4j
public class
TaskInfoServiceImpl extends ServiceImpl<TaskInfoMapper, TaskInfoDO> implements TaskInfoService {


    @Autowired
    private Scheduler scheduler;

    @Autowired
    ScannerAutoExecutionTask scannerAutoExecutionTask;

    @Override
    public TaskInfoDO getByDatasourceIdAndTenantId(Long datasourceId, String tenantId) {
        log.info("task service");
        return new TaskInfoDO();

    }

    @Override
    public Boolean run(Long taskId) throws ParseException, SchedulerException {
        scannerAutoExecutionTask.execute();
        return true;
    }


    /**
     * 这样，可以将定时任务移除掉，但是是无法暂停当前正在只想的定时任务的，
     * @param taskId
     * @return
     */
    @Override
    public Boolean stop(String taskId) {
        try {
            // 暂停触发器的计时
            scheduler.pauseTrigger(TriggerKey.triggerKey(taskId, Scheduler.DEFAULT_GROUP));
            // 移除触发器中的任务
            scheduler.unscheduleJob(TriggerKey.triggerKey(taskId, Scheduler.DEFAULT_GROUP));
            scheduler.deleteJob(JobKey.jobKey(taskId, Scheduler.DEFAULT_GROUP));
            log.info("{}任务停止成功", taskId);
        } catch (SchedulerException e) {
            log.error("{}任务停止失败", taskId, e);
        }
        return true;
    }

    public List<Object> getAllTaskKeys() {
        Set<TriggerKey> triggerKeys = null;
        try {
            triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(Scheduler.DEFAULT_GROUP));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return Arrays.asList(triggerKeys.toArray()
        );

    }

}


