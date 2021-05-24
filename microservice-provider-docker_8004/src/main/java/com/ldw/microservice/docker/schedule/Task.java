package com.ldw.microservice.docker.schedule;

import com.ldw.microservice.docker.constnat.RedisKeyPrefixConstant;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
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

    private volatile Thread thisThread;

    private volatile boolean isJobInterrupted = false;

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext context){
        String lockKey = RedisKeyPrefixConstant.LOCK_COURSE;
        RLock lock = redissonClient.getLock(lockKey);
        try {

            //尝试却拿锁，拿到就返回true，拿不到就返回false。不管拿不拿得到，都会立即返回
            //不会造成死锁，已经测试过。Redisson提供了一个监控锁的看门狗，它的作用是在Redisson实例被关闭前，
            // 不断的延长锁的有效期，也就是说，如果一个拿到锁的线程一直没有完成逻辑，
            // 那么看门狗会帮助线程不断的延长锁超时时间，锁不会因为超时而被释放。
            boolean res = lock.tryLock();
            if (res) {
                log.info("拿到了锁");
                thisThread = Thread.currentThread();
                for (int i = 0; i < 200; i++) {
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

            if (isJobInterrupted) {
                log.info("Job " + lockKey + " did not complete");
                throw new Exception("停止了        ");
            } else {
                log.info("Job " + lockKey + " not complete");
            }
        }

    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        log.info("Job  INTERRUPTING --");
        isJobInterrupted = true;
        if (thisThread != null) {
            // this call causes the ClosedByInterruptException to happen
            thisThread.interrupt();
        }
    }
}
