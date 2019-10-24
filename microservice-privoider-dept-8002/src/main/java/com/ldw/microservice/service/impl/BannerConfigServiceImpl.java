package com.ldw.microservice.service.impl;

import com.ldw.microservice.dao.BannerConfigDao;
import com.ldw.microservice.dao.DeptDao;
import com.ldw.microservice.entity.BannerConfig;
import com.ldw.microservice.entity.Dept;
import com.ldw.microservice.service.BannerConfigSercice;
import com.ldw.microservice.service.DeptSercice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerConfigServiceImpl implements BannerConfigSercice {

    @Autowired
    private BannerConfigDao bannerConfigDao;

    @Override
    public BannerConfig findById(String id) {
        return bannerConfigDao.findById(id);
    }

    public int addBannerConfig(BannerConfig bannerConfig) {
        return bannerConfigDao.addBannerConfig(bannerConfig);
    }


}
