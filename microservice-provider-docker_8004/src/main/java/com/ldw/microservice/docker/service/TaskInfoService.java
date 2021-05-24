package com.ldw.microservice.docker.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ldw.microservice.docker.eo.TaskInfoDO;
import org.quartz.SchedulerException;

import java.text.ParseException;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ldw
 * @since 2021-03-18
 */
public interface TaskInfoService extends IService<TaskInfoDO> {

    /**
     * 根据datasourceId 和tenantId 查找，只会返回一条记录
     * @param datasourceId
     * @return
     */
    TaskInfoDO getByDatasourceIdAndTenantId(Long datasourceId, String tenantId);


    Boolean run(Long taskId ) throws ParseException, SchedulerException;

    Boolean stop(String taskId);

    List getAllTaskKeys();
}


