package com.microservice.gateway.controller;


import com.microservice.gateway.config.AuthSkipUrlsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 描述: 认证接口
 *
 * @Auther: lzxouhhuhu
 * @Date: 2019/7/9 13:53
 */
@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    private @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    AuthSkipUrlsProperties authSkipUrlsProperties;




    /**
     * 登陆认证接口
     *
     * @param userDTO
     * @return
     */
    @RequestMapping("/login")
    public void login() {
        System.out.println("login");
    }


    @RequestMapping("getServiceList")
    public List getServiceList() {
        List list = discoveryClient.getServices();
        System.out.println("service list " + list);

        List<ServiceInstance> listServiceInstance = discoveryClient
                .getInstances("MICROSERVICE-DEPT");
        for (ServiceInstance serviceInstance : listServiceInstance) {
            System.out.println("getServiceId:" + serviceInstance.getServiceId()
                    + "\t getHost: " + serviceInstance.getHost()
                    + "\t  getPort:" + serviceInstance.getPort());
        }
        return list;
    }


/*
    @RequestMapping("redisTest")
    public void redisTest() {
        redisUtil.set("aa", "aa ");
        System.out.println(redisUtil.get("aa"));
    }
*/


    @RequestMapping("getApiUrls")
    public void getApiUrls() {
        authSkipUrlsProperties.getApiUrls().forEach(item -> {
            System.out.println(item);
        });
    }

}