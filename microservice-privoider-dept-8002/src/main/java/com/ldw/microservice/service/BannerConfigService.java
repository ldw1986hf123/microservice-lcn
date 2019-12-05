package com.ldw.microservice.service;

import com.ldw.microservice.entity.BannerConfig;
import com.ldw.microservice.entity.Dept;
import com.ldw.microservice.entity.OperLog;
import org.springframework.boot.Banner;

import java.util.List;

public interface BannerConfigService {
	BannerConfig findById(String id);
	OperLog modifyCurrency(String currency) throws Exception;
}
