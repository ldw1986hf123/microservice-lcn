package com.ldw.microservice.docker.controller;



import com.ldw.microservice.docker.service.TaskInfoService;
import com.ldw.microservice.docker.util.Payload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@Slf4j
public class TaskController {

    @Autowired
    TaskInfoService taskInfoService;


    /**
     * @param taskId
     * @return
     * @desc 运行任务 同aop的方法来做运行任务日志记录，不干扰主流程逻辑
     */
    @GetMapping("/run")
    public Payload<Map<String, Object>> run(@RequestParam("taskId") Long taskId) {
        Boolean result = false;
        try {
            result = taskInfoService.run(taskId);
        } catch (Exception e) {
            log.error("运行出错",e);
        }
        return new Payload(result);
    }
}
