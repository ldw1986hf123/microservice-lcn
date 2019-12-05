package com.ldw.microservice.service.impl;

import com.ldw.microservice.DeptProvider8002_App;
import com.ldw.microservice.service.BannerConfigService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
@SpringBootTest(classes = DeptProvider8002_App.class)
@RunWith(SpringRunner.class)
public class BannerConfigServiceImplTest {

    @Autowired
    BannerConfigService bannerConfigService;
    @Test
    public void findById() throws Exception {
        bannerConfigService.modifyCurrency("sdas");
    }

    @Test
    public void addBannerConfig() {
    }

    @Test
    public void staticBannerConfigClick() {
    }

    @Test
    public void main() {
    }
}