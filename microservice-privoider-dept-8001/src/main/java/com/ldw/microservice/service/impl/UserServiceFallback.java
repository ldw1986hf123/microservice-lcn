package com.ldw.microservice.service.impl;

import com.ldw.microservice.feign.UserServiceFeign;
import org.springframework.stereotype.Component;

@Component
public class UserServiceFallback implements UserServiceFeign {
    @Override
    public String getByDeptId(Long id) {
        return "User service unavailable, ID: " + id+"降级逻辑"; // 降级逻辑
    }
}