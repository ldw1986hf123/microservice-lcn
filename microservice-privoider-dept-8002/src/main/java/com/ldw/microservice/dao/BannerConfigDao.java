package com.ldw.microservice.dao;

import com.ldw.microservice.entity.BannerConfig;
import com.ldw.microservice.entity.Dept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@SuppressWarnings("rawtypes")
@Mapper
public interface BannerConfigDao {

	BannerConfig findById(String id);

	int addBannerConfig(BannerConfig bannerConfig);
}
