package com.microservice.gateway.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述: 认证接口
 *
 * @Auther: lzx
 * @Date: 2019/7/9 13:53
 */
@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController {

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


}