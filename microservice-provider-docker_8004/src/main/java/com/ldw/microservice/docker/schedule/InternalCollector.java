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
public class InternalCollector {


    @Autowired
    private Scheduler scheduler;


    /**
     * @desc 执行扫描，每分钟执行一次
     */
//    @Async
//    @Scheduled(cron = "0 */3 * * * ?")
    public void execute() {

    }


}
