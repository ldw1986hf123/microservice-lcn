package com.ldw.microservice.docker.schedule;

import com.ldw.microservice.docker.constnat.RedisKeyPrefixConstant;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author WangXf
 * @desc 任务
 * @date 2020年9月23日 下午5:32:05
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component
public class Task implements InterruptableJob {

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String lockKey = RedisKeyPrefixConstant.LOCK_COURSE;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean res = lock.tryLock();
            if (res) {
                log.info("拿到了锁");
                for (int i = 0; i < 20; i++) {
                    log.info("执行第{}次", i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            } else {
                log.info("没用拿到锁");
                return;
            }
        }/* catch (InterruptedException e) {
            e.printStackTrace();
        }*/ finally {
            log.info("释放了锁");
            lock.unlock();
        }

    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {

    }
}
