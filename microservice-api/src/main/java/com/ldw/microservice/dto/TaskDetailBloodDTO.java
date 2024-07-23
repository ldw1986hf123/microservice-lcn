package com.ldw.microservice.dto;

import com.deepexi.util.config.JsonDateSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/6 11:00
 * @Description 表血缘job VO
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class TaskDetailBloodDTO {

    /**
     * jobId
     */
    @ApiModelProperty(value = "jobId", example = "0")
    private Long jobId;

    /**
     * jobName
     */
    @ApiModelProperty(value = "job名", example = "测试job")
    private String jobName;


    @ApiModelProperty(value = "项目Id", example = "1")
    private Long projectId;

    @ApiModelProperty(value = "项目code", example = "test")
    private String projectCode;

    /**
     * 流程实例id
     */
    @ApiModelProperty(value = "流程实例id", example = "0")
    private Long processInstanceId;

    /**
     * 实例id
     */
    @ApiModelProperty(value = "实例id", example = "0")
    private Long instanceId;

    /**
     * 任务开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date startTime;

    /**
     * 任务结束时间
     */
    @JsonSerialize(using = JsonDateSerializer.class)
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    /**
     * 任务耗时 秒
     */
    @ApiModelProperty(value = "务耗时, 单位秒", example = "0")
    private Long timeConsuming;

    /**
     * 任务记录数
     */
    @ApiModelProperty(value = "任务记录数", example = "0")
    private Integer instanceNum;

}
