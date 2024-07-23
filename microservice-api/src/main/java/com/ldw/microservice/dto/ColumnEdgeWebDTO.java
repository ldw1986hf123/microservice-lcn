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
 * @Description 字段血缘边VO
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class ColumnEdgeWebDTO {

    /**
     * @desc 来源字段id
     */
    @ApiModelProperty(value = "来源字段id", example = "0")
    private Long sourceColumnId;

    @ApiModelProperty(value = "来源字段code", example = "name1")
    private String sourceColumnName;

    /**
     * @desc 目标字段id
     */
    @ApiModelProperty(value = "目标字段id", example = "0")
    private Long targetColumnId;

    @ApiModelProperty(value = "目标字段code", example = "name2")
    private String targetColumnName;


    @ApiModelProperty(value = "job列表")
    private List<TaskDetailBloodDTO> tasks;
}
