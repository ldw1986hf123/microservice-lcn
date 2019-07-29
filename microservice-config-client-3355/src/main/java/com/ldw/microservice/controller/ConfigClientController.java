package com.ldw.microservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("config")
public class ConfigClientController {
    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${eureka.client.service-url.defaultZone}")
    private String defaultZone;
    @Value("${server.port}")
    private String port;


    @RequestMapping("getConfig1")
    public String getConfig() {
        String s = "application name:" + applicationName + " defaultZone:  " + defaultZone + " eureka servers" + defaultZone + "port :" + port;
        System.out.println(s);
        return s;
    }

}
