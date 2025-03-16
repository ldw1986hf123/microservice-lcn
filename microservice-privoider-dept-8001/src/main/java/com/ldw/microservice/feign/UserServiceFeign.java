package com.ldw.microservice.feign;

import com.ldw.microservice.service.impl.UserServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "microservice-uc",fallback = UserServiceFallback.class)
public interface UserServiceFeign {
    @GetMapping("getUserById")
    String getByDeptId(@PathVariable("deptId") Long deptId);
}