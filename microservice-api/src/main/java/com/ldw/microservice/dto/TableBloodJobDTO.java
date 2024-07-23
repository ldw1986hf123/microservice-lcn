package com.ldw.microservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/4 15:06
 * @Description 表血缘job VO
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class TableBloodJobDTO {

    private Long jobId;

    private String jobName;

    /**
     * job状态，0-下线，1-在线
     */
    private Integer jobStatus;

    /**
     * 业务流程实例ID
     */
    private Long processInstanceId;

    /**
     * 实例ID
     */
    private Long instanceId;

    /**
     * 项目Id
     */
    private Long projectId;

    /**
     * 项目code
     */
    private String projectCode;

    /**
     * 任务开始时间
     */
    private Date startTime;

    /**
     * 任务结束时间
     */
    private Date endTime;

    /**
     * 任务耗时 秒
     */
    private Long timeConsuming;

}
