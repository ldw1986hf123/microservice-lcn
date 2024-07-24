package com.ldw.microservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.deepexi.bigdata.develop.api.TaskExecuteApi;
import com.deepexi.bigdata.develop.domain.dto.*;
import com.deepexi.daas.common.config.InternalPayload;
import com.deepexi.data.metadata.biz.DevelopTaskBizService;
import com.deepexi.data.metadata.domain.dto.blood.TaskDetailDTO;
import com.deepexi.data.metadata.domain.vo.blood.TaskDetailVO;
import com.deepexi.util.pageHelper.PageBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/9 15:25
 * @Description
 */
@Slf4j
@Service
public class DevelopTaskBizServiceImpl implements DevelopTaskBizService {

    @Autowired
    private TaskExecuteApi taskExecuteApi;

    @Override
    public TaskDetailVO getTaskDetail(String tenantId, String projectCode, Long projectId,  Long jobId, String jobName) {
        if (StringUtils.isBlank(projectCode)) {
            return null;
        }
        TaskDetailVO taskDetail = new TaskDetailVO();
        TaskInstancesBloodDTO bloodDTO = new TaskInstancesBloodDTO();
        bloodDTO.setPageNo("1");
        bloodDTO.setPageSize("1");
        bloodDTO.setTenantId(tenantId);
        List<Long> jobIds = Arrays.asList(jobId);
        bloodDTO.setTaskId(JSON.toJSONString(jobIds));
        if (null != projectId) {
            bloodDTO.setProjectId(String.valueOf(projectId));
        }
        bloodDTO.setProjectCode(projectCode);
        bloodDTO.setProjectName(projectCode);
        bloodDTO.setProcessInstanceId(0);
        log.info("请求参数：{}", JSON.toJSONString(bloodDTO));
        InternalPayload<TaskInstancesBloodDTOPage> internalPayload = null;
        try {
            internalPayload = taskExecuteApi.getTaskInstanceBloodList(bloodDTO);
            log.info("getTaskInstanceBloodList result={}", internalPayload);
        } catch (Exception e) {
            log.error("get task instance failed, taskId={}, message={}", jobId, e.getMessage());
            return null;
        }
        TaskInstancesBloodDTOPage payload = internalPayload.getPayload();
        if(null != payload) {
            Integer total = payload.getTotal();
            taskDetail.setInstanceNum(total);
            List<TaskInstancesBlood> instancesBloods = payload.getTotalList();
            if(!CollectionUtils.isEmpty(instancesBloods)) {
                TaskInstancesBlood taskInstancesBlood = instancesBloods.get(0);
                taskDetail.setProcessInstanceId((long)taskInstancesBlood.getProcessInstanceId());
                taskDetail.setInstanceId((long) taskInstancesBlood.getId());
                Date startTime = taskInstancesBlood.getStartTime();
                Date endTime = taskInstancesBlood.getEndTime();
                taskDetail.setStartTime(startTime);
                taskDetail.setEndTime(endTime);
                if (null != startTime && null != endTime) {
                    taskDetail.setTimeConsuming((endTime.getTime() - startTime.getTime())/1000);
                }
            }
        }
        taskDetail.setJobId(jobId);
        taskDetail.setJobName(jobName);
        taskDetail.setProjectId(projectId);
        taskDetail.setProjectCode(projectCode);
        return taskDetail;
    }

    @Override
    public PageBean<TaskDetailDTO> getTaskDetails(String tenantId, String projectCode, Long projectId, List<Long> jobIds, Integer page, Integer size) {
        PageBean pageBean = new PageBean();
        if (StringUtils.isBlank(projectCode)) {
            return null;
        }
        List<String> jobIdsStr = new ArrayList<>();
        jobIds.stream().forEach(j->{
            jobIdsStr.add(String.valueOf(j));
        });
        TaskInstanceQueryDTO bloodDTO = new TaskInstanceQueryDTO();
        bloodDTO.setPageNo(page);
        bloodDTO.setPageSize(size);
        bloodDTO.setTenantId(tenantId);
        bloodDTO.setTaskIds(jobIdsStr);
        bloodDTO.setProjectId(String.valueOf(projectId));
        log.info("请求参数：{}", JSON.toJSONString(bloodDTO));
        InternalPayload<TaskInstanceManDTOPage> internalPayload = null;
        try {
            internalPayload = taskExecuteApi.getJobInstanceNoProjectName(bloodDTO);
            log.info("getTaskInstanceBloodList result={}", internalPayload.toString());
        } catch (Exception e) {
            log.error("get task instance failed, taskId={}, message={}", jobIds, e.getMessage());
            return null;
        }
        TaskInstanceManDTOPage payload = internalPayload.getPayload();
        if(null != payload) {
            List<TaskInstanceManDTO> instancesBloods = payload.getTotalList();
            if(!CollectionUtils.isEmpty(instancesBloods)) {
                List<TaskDetailDTO> taskDetailDtos = instancesBloods.stream().map(instance -> {
                    TaskDetailDTO taskDetail = new TaskDetailDTO();
                    if (StringUtils.isNotBlank(instance.getProcessInstanceId())) {
                        taskDetail.setProcessInstanceId(Long.parseLong(instance.getProcessInstanceId()));
                    }
                    taskDetail.setInstanceId((long) instance.getId());
                    Date startTime = instance.getStartTime();
                    Date endTime = instance.getEndTime();
                    taskDetail.setStartTime(startTime);
                    taskDetail.setEndTime(endTime);
                    taskDetail.setJobId(Long.parseLong(instance.getTaskId()));
                    if (null != startTime && null != endTime) {
                        taskDetail.setTimeConsuming((endTime.getTime() - startTime.getTime()) / 1000);
                    }
                    return taskDetail;
                }).collect(Collectors.toList());
                pageBean.setContent(taskDetailDtos);
                pageBean.setNumberOfElements(taskDetailDtos.size());
            }
            pageBean.setTotalElements(payload.getTotal());
            pageBean.setTotalPages(payload.getTotalPage());
            pageBean.setSize(size);
            pageBean.setNumber(page);
        }
        return pageBean;
    }
}
