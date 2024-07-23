package com.ldw.microservice.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/5 11:00
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TaskDetailQuery {
    /**
     * jobId
     */
    @ApiModelProperty(value = "jobId", example = "0")
    private Long jobId;

    /**
     * 任务名
     */
    @ApiModelProperty(value = "任务名", example = "测试任务")
    private String jobName;
    /**
     * 项目code
     */
    @ApiModelProperty(value = "任务code", example = "任务code")
    private String projectCode;
    /**
     * 任务Id
     */
    @ApiModelProperty(value = "项目Id", example = "0")
    private Long projectId;
    /**
     * 任务名
     */
    @ApiModelProperty(value = "job状态", example = "0")
    private Integer status;
}
