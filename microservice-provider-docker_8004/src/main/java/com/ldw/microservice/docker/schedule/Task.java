package com.ldw.microservice.docker.schedule;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

/**
 * @author WangXf
 * @desc 任务
 * @date 2020年9月23日 下午5:32:05
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component
public class Task implements Job {


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("{} 执行", DateUtil.now());
        for (int i = 0; i < 300; i++) {
            log.info("执行第{}次",i);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}
