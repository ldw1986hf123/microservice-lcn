package com.ldw.microservice.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/6 11:00
 * @Description 表血缘边VO
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class TableEdgeDTO {

    /**
     * @desc 来源表id
     */
    @ApiModelProperty(value = "来源表id", example = "0")
    private Long sourceTableId;

    /**
     * @desc 来源表code eg:user
     */
    @ApiModelProperty(value = "来源表code", example = "user1")
    private String sourceTableName;

    /**
     * @desc 目标表id
     */
    @ApiModelProperty(value = "目标表id", example = "0")
    private Long targetTableId;
    /**
     * @desc 目标表code eg:user
     */
    @ApiModelProperty(value = "目标表code", example = "user2")
    private String targetTableName;

    @ApiModelProperty(value = "job列表")
    private List<TaskDetailBloodDTO> tasks;
}
