package com.ldw.microservice.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/6 11:00
 * @Description 影响分析VO
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class TableBloodInfluenceDownLoadDTO {

    /**
     * @desc 层级
     */
    @ApiModelProperty(value = "所在层级", example = "0")
    private Integer level;
    /**
     * @desc 表名
     */
    @ApiModelProperty(value = "表名", example = "user")
    private String tableName;
    /**
     * @desc owner
     */
    @ApiModelProperty(value = "拥有人", example = "bob")
    private String owner;
    /**
     * @desc 最新产出任务id jobId
     */
    @ApiModelProperty(value = "最新产出任务名", example = "test")
    private String jobName;

    /**
     * @desc 最新产出任务id jobId
     */
    @ApiModelProperty(value = "最新产出任务id", example = "0")
    private Long jobId;
    /**
     * @desc job状态
     */
    @ApiModelProperty(value = "job状态，0-下线，1-在线", example = "1")
    private String jobStatusStr;
}
