package com.ldw.microservice.service.impl;

import com.ldw.microservice.entity.BannerConfig;
import com.ldw.microservice.service.BannerConfigSercice;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
/** 注入相关的配置文件：可以写入多个配置文件 **/
@ContextConfiguration(locations = {"classpath:application.yml"})
public class BannerConfigServiceImplTest {

    @Autowired
    BannerConfigServiceImpl bannerConfigSercice;

    @org.junit.Test
    public void findById() {
        System.out.println(bannerConfigSercice.findById("1"));
    }

    @org.junit.Test
    public void addBannerConfig() {
        BannerConfig bannerConfig=new BannerConfig();
        bannerConfig.setBannerName("a");
        bannerConfig.setImageURL("asda");
        bannerConfig.setClientId("1");
        bannerConfigSercice.addBannerConfig(bannerConfig);
    }
}