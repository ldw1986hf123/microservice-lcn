package com.ldw.microservice.service.impl;

import com.ldw.microservice.entity.BannerConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @program: useSwagger
 * @description:
 * @author: zxb
 * @create: 2019-10-24 23:22
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class BannerJunitTest {
    @Autowired
    private BannerConfigServiceImpl bannerConfigService;

    @Test
    public void insertUser() {
        System.out.println(bannerConfigService.findById("1"));
    }


    @Test
    public void addTest() {
        BannerConfig bannerConfig = new BannerConfig();
        bannerConfig.setId("12");
        bannerConfig.setBannerName("a");
        bannerConfig.setImageURL("asda");
        bannerConfig.setClientId("1");
        System.out.println(bannerConfigService.addBannerConfig(bannerConfig));
    }

}
