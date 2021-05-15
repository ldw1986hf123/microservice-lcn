package com.ldw.microservice.docker.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldw.microservice.docker.eo.TaskInfoDO;
import com.ldw.microservice.docker.mapper.TaskInfoMapper;
import com.ldw.microservice.docker.service.TaskInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ldw
 * @since 2021-03-18
 */
@Service
@Slf4j
public class
TaskInfoServiceImpl extends ServiceImpl<TaskInfoMapper, TaskInfoDO> implements TaskInfoService {


    @Override
    public TaskInfoDO getByDatasourceIdAndTenantId(Long datasourceId, String tenantId) {
        log.info("task service");
        return new TaskInfoDO();

    }

    @Override
    public Boolean run(Long taskId) {
        return false;

    }
}


