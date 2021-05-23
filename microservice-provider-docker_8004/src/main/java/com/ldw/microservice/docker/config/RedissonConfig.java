package com.ldw.microservice.docker.config;

import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {


    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("rediss://127.0.0.1:6379");

        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
