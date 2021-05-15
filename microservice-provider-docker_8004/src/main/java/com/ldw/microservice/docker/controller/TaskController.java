package com.ldw.microservice.docker.controller;


import com.ldw.microservice.docker.service.TaskInfoService;
import com.ldw.microservice.docker.util.Payload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public Payload<Map<String, Object>> run() {
        Boolean result = false;
        try {
            result = taskInfoService.run(234L);
        } catch (Exception e) {
            log.error("运行出错", e);
        }
        return new Payload(result);
    }

    /**
     * @param taskId
     * @return
     * @desc 运行任务 同aop的方法来做运行任务日志记录，不干扰主流程逻辑
     */
    @GetMapping("/stop/{taskId}")
    public Payload stop(@PathVariable("taskId") String taskId) {
        Boolean result = false;
        log.info("任务id{}    ", taskId);

        try {
            result = taskInfoService.stop( taskId );
        } catch (Exception e) {
            log.error("运行出错", e);
        }
        return new Payload(result);
    }

    @GetMapping("/getAllTaskKeys")
    public Payload getAllTaskKeys() {
        log.info("getAllTaskKeys kkkkss**************");
        List result = null;
        try {
            result = taskInfoService.getAllTaskKeys();
        } catch (Exception e) {
            log.error("运行出错", e);
        }
        return new Payload(result);
    }


}
