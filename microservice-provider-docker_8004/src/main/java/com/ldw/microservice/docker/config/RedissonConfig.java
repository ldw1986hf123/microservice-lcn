package com.ldw.microservice.docker.config;

import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@Lazy
public class RedissonConfig {


    @Bean
    @Lazy
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        //设置看门狗的时间，不配置的话默认30000
        config.setLockWatchdogTimeout(12000);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
