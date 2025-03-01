package com.ldw.microservice.controller;

import com.ldw.microservice.feign.UserServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ldw.common.vo.Result;

@RestController
public class DepController {
    @Autowired
    UserServiceFeign userServiceFeign;

    @GetMapping("/getUsersByDeptId")
    public Result getUsersByDeptId(Long deptId) {
        userServiceFeign.getByDeptId(deptId);
        return Result.success("steven");
    }
}
