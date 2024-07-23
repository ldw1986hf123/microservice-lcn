package com.ldw.microservice.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/3 11:00
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DetailQuery {
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
     * 项目id
     */
    @ApiModelProperty(value = "项目id", example = "1")
    private Long projectId;

    /**
     * 项目名
     */
    @ApiModelProperty(value = "项目名", example = "test")
    private String projectCode;

    /**
     * job状态，0-下线,1-在线
     */
    @ApiModelProperty(value = "job状态，0-下线,1-在线", example = "0")
    private Integer status;
}
