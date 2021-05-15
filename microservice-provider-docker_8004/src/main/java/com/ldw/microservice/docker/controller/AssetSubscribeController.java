package com.ldw.microservice.docker.controller;


import com.ldw.microservice.docker.service.TaskInfoService;
import com.sun.jmx.snmp.tasks.TaskServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2020. All rights reserved.
 *
 * @description:
 * @author: ludanwen
 * @time: 2021/3/23 10:13
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/asset/subscribe")
public class AssetSubscribeController {

    @Autowired
    TaskInfoService taskInfoService;

    @RequestMapping(value = "/subscribe", method = RequestMethod.POST)
    public void subscribe() {
        taskInfoService.getByDatasourceIdAndTenantId(34234L, "dasd");
    }


}
